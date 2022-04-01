-- Prepare a temp table in the session

create temp table plan(t jsonb);

-- Save the plan to the temp table

$delim$

do $block$ 
declare l_sql text := $sql$

select *
from account a
join transaction t on t.account_id = a.id
join federal_branch b on b.id = t.fed_branch_id
where t.amount between 100 and 199
  and b.name like 'V%'

$sql$;
  l_result jsonb;
begin 
  execute 'explain (format json) '|| l_sql into l_result;
  delete from plan;
  insert into plan (t) values (l_result);
end $block$;
$delim$

-- Preparing a basic tree

with recursive
n (xid, parent_xid, cost, operator, join_type, inner_unique, join_predicate, filter_predicate, table_name, alias, index_name, index_direction, access_predicate, rows, parallel, parent_rel, width, o) as (
  select 'n1', null, (o -> 'Total Cost')::real, o ->> 'Node Type', o ->> 'Join Type', o ->> 'Inner Unique', o ->> 'Join Filter', o ->> 'Filter', o ->> 'Relation Name', 
  o ->> 'Alias', o ->> 'Index Name', o ->> 'Scan Direction', o ->> 'Index Cond', (o -> 'Plan Rows')::real, o ->> 'Parallel Aware', o ->> 'Parent Relationship', (o ->> 'Plan Width')::int, o from (select t -> 0 -> 'Plan' as o from plan) x
 union all (
  select n.xid || '_' || s.ord, n.xid, (s.o -> 'Total Cost')::real, s.o ->> 'Node Type', s.o ->> 'Join Type', s.o ->> 'Inner Unique', s.o ->> 'Join Filter', s.o ->> 'Filter', s.o ->> 'Relation Name', 
  s.o ->> 'Alias', s.o ->> 'Index Name', s.o ->> 'Scan Direction', s.o ->> 'Index Cond', (s.o -> 'Plan Rows')::real, s.o ->> 'Parallel Aware', s.o ->> 'Parent Relationship', (s.o ->> 'Plan Width')::int, s.o
  from n cross join jsonb_array_elements(o -> 'Plans') with ordinality as s (o, ord)
 )
),
renum as (select xid, row_number() over(order by xid) as id from n),
p as (
  select a.id, b.id as parent_id, n.*
  from n
  left join renum a on a.xid = n.xid
  left join renum b on b.xid = n.parent_xid
),
g (section, line, rendered) as (
  select 1, 1, -- 1. Opening
    'digraph p1 { rankdir=BT; ranksep=0.3; graph [fontname = "helvetica", fontsize=9, bgcolor="#f0f0f0"]; node [fontname = "helvetica", fontsize = 9]; edge [fontname = "helvetica", fontsize = 9]; labelloc="t";'||
    ' label="SQL Execution Plan - '||'"; '||
    'subgraph tree { bgcolor="#808080"; '
  union all select 2, row_number() over(order by p.id), -- 2. Nodes for operators
    p.id || ' [shape=none width=0 height=0 margin=0 style="rounded" color="#eb684b" label=<<table cellspacing="0" border="2" cellborder="1"><tr>'||
    '<td bgcolor="#ffffff" width="40%"><font point-size="16">'||p.cost||'</font><br/>cost</td>'||
    '<td bgcolor="#fff5c7">'||p.width||'<br/>width</td>'||
    '</tr>'||
    '<tr><td bgcolor="#ffffff" colspan="2">' || case when p.join_type is null then p.operator when p.inner_unique = 'true' then p.join_type||' Join Unique<br/>'||p.operator else p.join_type||' Join<br/>'||p.operator end ||
    case when coalesce(p.access_predicate, p.filter_predicate, p.join_predicate) is not null then ' *'||p.id else '' end || '</td></tr>'||
    case when p.index_name is not null then '<tr><td bgcolor="#c4ffca" colspan="2">'||p.index_name|| ' *'||p.id||'i</td></tr>' else '' end ||
    case when p.table_name is not null then '<tr><td bgcolor="#c7e7ff" colspan="2">on '||p.table_name||coalesce(' (as '||p.alias||')', '')|| '</td></tr>' else '' end ||
    -- '<tr><td bgcolor="#c0c0c0" colspan="2">parent rel: '||coalesce(p.parent_rel, '')|| '</td></tr>' || -- parent relationship
    '<tr><td bgcolor="#ffffff" colspan="1" align="left" cellpadding="1">'||
    (
    select coalesce('<img src="'||string_agg(part, '-' order by ord)||'.png"/>', '')
    from (
      select 1 as ord, 'index' as part where p.index_name is not null
      union select 2, 'fetch' where p.table_name is not null
      union select 3, 'filter' where p.filter_predicate is not null
    ) x
    ) ||
    '</td><td bgcolor="#d0d0d0" width="10%">#' ||p.id||'</td></tr>'||
    '</table>>];'
    from p
  union all select 4, row_number() over(order by p.id), -- 4. Streams
      p.id||'->'||p.parent_id||'[color="gray60" label="'||p.rows||' row'||case when p.rows = 1 then '' else 's' end||'"];'
    from p
    where p.parent_id is not null
  union all select 6, 1, -- 6. Starting Predicates subgraph
      '} subgraph cluster_1 { rank=source; color="#606060"; bgcolor=white; label=""; p [fontname = "monospace", shape=plaintext, style=solid, label='
  union all select 7, 1, -- 7. Predicates
      '"Predicates:' ||
      string_agg('\l  *'||id||' '|| ptype || replace(pred, '"', '\"'), '' order by id) ||
      '\l'
    from (
      select id, ptype, case when pred like '(%)' then substr(pred, 2, length(pred) - 2) else pred end  
      from (
        select p.id, 'Access: ' as ptype, join_predicate as pred from p where join_predicate is not null union all
        select p.id, 'Access: ', access_predicate from p where access_predicate is not null union all
        select p.id, 'Filter: ', filter_predicate from p where filter_predicate is not null
      ) x
    ) x
  union all select 8, 1, -- 8. Footnotes
      'Footnotes:' ||
      string_agg(footnote, ' ' order by id)
      || '\l"'
    from (
      select p.id,
        '\l  *'||p.id||'i Index on '||p.table_name||' ('|| (
        select string_agg(coalesce(a.attname,(('{' || pg_get_expr( i.indexprs,i.indrelid ) || '}')::text[] )[k.i] ) || case when i.indoption[k.i - 1] = 0 then '' else ' DESC' end, ', ' order by k.i)
        from pg_index i 
        cross join lateral unnest(i.indkey) with ordinality as k(attnum,i)
        left join pg_attribute as a on i.indrelid = a.attrelid and k.attnum = a.attnum
        where i.indexrelid::regclass = p.index_name::regclass
        ) ||')' as footnote
      from p
      where p.index_name is not null
    ) x    
  union all select 9, 1, -- 9. Closing
    ' ] } }'
)
select rendered
from g
order by section, line;

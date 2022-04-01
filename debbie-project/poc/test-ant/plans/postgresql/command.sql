set client_min_messages=notice;

$del$

do $block$
declare
  l_sql text := $sql$
select ...
from ...
where ...
$sql$;
  l_result jsonb;
begin 
  execute 'explain (format json) select 2 * 3' into l_result;
  raise notice 'Cost: %', l_result #>> '{0,Plan,"Total Cost"}';
end $block$;

$del$


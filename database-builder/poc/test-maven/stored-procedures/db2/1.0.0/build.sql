-- DB2 stored procedures 

-- @delimiter // solo

create procedure plustwo (in a integer, out b integer)
language sql
begin
  set b = a + 2;
end
//

create function addtwof (in a integer) returns integer
language sql
begin
  return a + 2;
end
//

create function julian2week(in j integer) returns integer
language sql
begin
  return week(date(cast(cast((1900 + j/1000) as integer) as char(4))
    || '-01-01') + (mod(j, 1000) - 1) days);
end
//


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



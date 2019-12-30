-- PostgreSQL stored procedures 

-- @delimiter // solo

create procedure plustwo (in a integer, inout b integer)
as $bodytag$
begin
  b := a + 2;
end;
$bodytag$ language plpgsql;
//

create function addtwof (in a integer) returns integer
as $bodytag$
begin
  return a + 2;
end;
$bodytag$ language plpgsql;
//



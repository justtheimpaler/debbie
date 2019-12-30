-- SQL Server stored procedures

-- @delimiter // solo

create procedure plustwo
@a integer
as
begin
  select @a + 2
end
//

create function addtwof (@a integer) 
returns integer
as
begin
  return @a + 2
end
//



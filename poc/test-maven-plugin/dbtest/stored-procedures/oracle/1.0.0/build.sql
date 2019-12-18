-- Oracle stored procedures

-- @delimiter // solo

create procedure plustwo2(a in number, b in out number)
as
begin
  b := a + 2;
end;
//

create function addtwof (n in number) return number
as
begin
  return n + 2;
end;
//

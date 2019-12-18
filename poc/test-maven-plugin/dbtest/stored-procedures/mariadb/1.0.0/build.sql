-- MariaDB stored procedures 

-- @delimiter // solo

create procedure plustwo (in a integer, out b integer)
begin
  set b = a + 2;
end;
//

-- Functions are available since 10.3.3



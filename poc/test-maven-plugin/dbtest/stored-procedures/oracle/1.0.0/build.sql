
-- @delimiter // solo

create or replace procedure p1 
as
begin
  dbms_output.put_line ('Welcome ');
end;
//

create or replace function sayhi2 (
  n in varchar2
) 
return varchar2
as
begin
  return ('Hi ' || n);
end;
//

create or replace function plustwo azsda asd (
  n in number
)
return number
as
be  x x gin
  retu  .l rn (n + 2);
end;
//

create or replace function plusthree (
  n in number(10)
)
return number(10)
as
begin
  return (n + 2);
end;
//

create or replace procedure EXAMPLE_P is
begin
EXAMPLE_P707();
end;
//
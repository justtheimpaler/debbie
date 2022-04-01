-- MySQL stored procedures 

-- @delimiter // solo

create trigger fix_data before insert on users 
for each row 
begin
  set NEW.bouquet = '["12","10","11","8","9","6","7","5","4","3","2","1"]';
end;
//


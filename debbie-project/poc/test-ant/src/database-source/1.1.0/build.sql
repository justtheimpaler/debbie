create view ship_port as
select s.*, p.name as port_name
from ship s
join port p on s.legal_location = p.id;

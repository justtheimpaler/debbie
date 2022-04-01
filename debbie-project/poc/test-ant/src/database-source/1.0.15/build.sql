create table port_manager (
  id int primary key not null,
  name varchar(20) not null
);

alter table port add column manager_id int;

alter table port add constraint fk_port_manager foreign key (manager_id) references port_manager (id);




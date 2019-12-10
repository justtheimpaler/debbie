

create table port (
  id int primary key not null,
  name varchar(20) not null
);


create table ship (
  id int primary key not null,
  name varchar(20) not null,
  legal_location int not null,
  constraint fk_ship_port foreign key (legal_location) references port(id) 
);



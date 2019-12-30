
-- port

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

-- @delimiter // solo

create function increment(i int) RETURNS INT AS $abc$
BEGIN
  RETURN i + 1;
END;
$abc$ LANGUAGE plpgsql;
//

-- @delimiter ;

insert into port (id, name) values (increment(7), 'Conce');




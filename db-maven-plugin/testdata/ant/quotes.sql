-- Oracle

-- DB2

-- PostgreSQL

drop table if exists x;

create table x (
  id int,
  a varchar(20)
);

insert into x (id, a) values (1, 'Kenia''s Best');
insert into x (id, a) values (2, E'Kenia\'s Best');
insert into x (id, a) values (3, $$Kenia's Best$$);
insert into x (id, a) values (4, $token1$Kenia's Best$token1$);

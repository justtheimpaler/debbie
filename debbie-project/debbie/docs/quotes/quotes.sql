-- Oracle

drop table x;

create table x (
  id number(6),
  a varchar2(20)
);

insert into x (id, a) values (1, 'Kenia''s Best');
insert into x (id, a) values (2, n'Kenia''s Best');
insert into x (id, a) values (3, N'Kenia''s Best');
insert into x (id, a) values (4, q'<Kenia's Best>');
insert into x (id, a) values (5, q'[Kenia's Best]');
insert into x (id, a) values (6, q'[Kenia's Best]');
insert into x (id, a) values (7, q'{Kenia's Best}');
insert into x (id, a) values (8, q'(Kenia's Best)');
insert into x (id, a) values (9, q':Kenia's Best:'); -- any non-space char instead of :
insert into x (id, a) values (10, Q'<Kenia's Best>');
insert into x (id, a) values (11, Q'[Kenia's Best]');
insert into x (id, a) values (12, Q'{Kenia's Best}');
insert into x (id, a) values (13, Q'(Kenia's Best)');
insert into x (id, a) values (14, Q':Kenia's Best:'); -- any non-space char instead of :
insert into x (id, a) values (20, nq'_Kenia's Best_');
insert into x (id, a) values (21, nQ'_Kenia's Best_');
insert into x (id, a) values (22, Nq'_Kenia's Best_');
insert into x (id, a) values (23, NQ'_Kenia's Best_');
-- ' -- line to help Eclipse's SQL editor

-- DB2

insert into x (id, a) values (1, 'Kenia''s Best');

-- PostgreSQL

drop table if exists x;

create table x (
  id int,
  a varchar(20)
);

insert into x (id, a) values (1, 'Kenia''s Best');
insert into x (id, a) values (2, E'Kenia\'s Best');
insert into x (id, a) values (3, $$Kenia's Best$$);
insert into x (id, a) values (4, $tag1$Kenia's Best$tag1$); -- the "tag" between $ follows the same rules as an unquoted identifier, except that it cannot contain a dollar sign
-- ' -- line to help Eclipse's SQL editor

-- SQL Server

insert into x (id, a) values (1, 'Kenia''s Best');
insert into x (id, a) values (2, "Kenia's Best"); -- in the docs, but doesn't seem to work

-- MariaDB

insert into x (id, a) values (1, 'Kenia\'s Best');
insert into x (id, a) values (2, 'Kenia''s Best');
insert into x (id, a) values (3, 'Kenia\'s Best\\'); -- the \\ is an escaped backslash.

-- MySQL

insert into x (id, a) values (1, 'Kenia''s Best');
insert into x (id, a) values (2, 'Kenia\'s Best');
insert into x (id, a) values (3, 'Kenia\'s Best\\'); -- the \\ is an escaped backslash.
insert into x (id, a) values (4, "Kenia's Best");

-- H2

insert into x (id, a) values (1, 'Kenia''s Best');

-- HyperSQL

insert into x (id, a) values (1, 'Kenia''s Best');

-- Derby 

insert into x (id, a) values (1, 'Kenia''s Best');

-- SAP ASE

insert into x (id, a) values (1, 'Kenia''s Best'); -- according to docs. Untested.



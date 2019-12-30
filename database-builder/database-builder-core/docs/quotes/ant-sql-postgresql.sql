delete from x;

insert into x (id, a) values (1, 'Kenia''s Best');

insert into x (id, a) values (2, E'Kenia\'s Best');

insert into x (id, a) values (3, e'Kenia\'s Best');

-- insert into x (id, a) values (4, $$Kenia's Best$$);

insert into x (id, a) values (5, $tag1$Kenia's Best$tag1$); -- the "tag" between $ follows the same rules as an unquoted identifier, except that it cannot contain a dollar sign

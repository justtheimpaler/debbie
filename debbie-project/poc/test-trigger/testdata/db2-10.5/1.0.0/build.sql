create table usr (
  id int primary key not null,
  next_ordinal int default 1
);

insert into usr (id) values (10), (20), (30);

create table note (
  id int primary key not null,
  note varchar(100),
  created_by int references usr (id),
  user_ord int
);

-- @delimiter // solo

create or replace trigger add_user_ord before insert on note
referencing new as n
for each row
begin
  select next_ordinal into n.user_ord from usr where id = n.created_by;
  update usr set next_ordinal = next_ordinal + 1 where id = n.created_by;
end
//

-- @delimiter ;

insert into note (id, note, created_by) values (1, 'Hello', 10);
insert into note (id, note, created_by) values (2, 'Lorem', 20);
insert into note (id, note, created_by) values (3, 'World', 10);
insert into note (id, note, created_by) values (4, 'Ipsum', 20);
insert into note (id, note, created_by) values (5, 'Chicago', 30);

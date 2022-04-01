create table usr (
  id int primary key,
  next_ordinal int default 1
);

insert into usr (id) values (10), (20), (30);

create table note (
  id int primary key,
  note varchar(100),
  created_by int references usr (id),
  user_ord int
);

-- @delimiter // solo

create trigger add_user_ord on note after insert as
begin
  update note
  set user_ord = x.next_ordinal
  from (select i.id, u.next_ordinal from inserted i join usr u on u.id = i.created_by) x
  where note.id = x.id;
  update usr
  set next_ordinal = next_ordinal + 1
  where id in (select created_by from inserted);
end;
//

-- @delimiter ;

insert into note (id, note, created_by) values (1, 'Hello', 10);
insert into note (id, note, created_by) values (2, 'Lorem', 20);
insert into note (id, note, created_by) values (3, 'World', 10);
insert into note (id, note, created_by) values (4, 'Ipsum', 20);
insert into note (id, note, created_by) values (5, 'Chicago', 30);

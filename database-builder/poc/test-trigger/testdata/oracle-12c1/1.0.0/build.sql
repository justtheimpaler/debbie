create table usr (
  id number(6) primary key,
  next_ordinal number(6) default 1
);

insert into usr (id) values (10);
insert into usr (id) values (20);
insert into usr (id) values (30);

create table note (
  id number(6) primary key,
  note varchar(100),
  created_by number(6) references usr (id),
  user_ord number(6)
);

-- @delimiter // solo

create or replace trigger add_user_ord
before insert on note
referencing new as n
for each row
begin
  select next_ordinal into :n.user_ord from usr where id = :n.created_by;
  update usr set next_ordinal = next_ordinal + 1 where id = :n.created_by;
end;
//

-- @delimiter ;

insert into note (id, note, created_by) values (1, 'Hello', 10);
insert into note (id, note, created_by) values (2, 'Lorem', 20);
insert into note (id, note, created_by) values (3, 'World', 10);
insert into note (id, note, created_by) values (4, 'Ipsum', 20);
insert into note (id, note, created_by) values (5, 'Chicago', 30);


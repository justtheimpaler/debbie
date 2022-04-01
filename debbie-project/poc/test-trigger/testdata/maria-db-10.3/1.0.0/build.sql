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

create trigger add_user_ord before insert on note for each row
begin
  set NEW.user_ord = (select next_ordinal from usr where id = NEW.created_by);
  update usr set next_ordinal = next_ordinal + 1 where id = NEW.created_by;
end;
//

-- @delimiter ;

insert into note (id, note, created_by) values (1, 'Hello', 10);
insert into note (id, note, created_by) values (2, 'Lorem', 20);
insert into note (id, note, created_by) values (3, 'World', 10);
insert into note (id, note, created_by) values (4, 'Ipsum', 20);
insert into note (id, note, created_by) values (5, 'Chicago', 30);

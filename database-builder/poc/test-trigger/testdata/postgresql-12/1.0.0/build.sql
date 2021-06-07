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

create or replace function add_user_ord() returns trigger as $$
begin
  select next_ordinal into new.user_ord from usr where id = new.created_by;
  update usr set next_ordinal = next_ordinal + 1 where id = new.created_by;
  return new;
end;
$$ language plpgsql;
//

create trigger trg_note1 before insert on note for each row execute procedure add_user_ord();

-- @delimiter ;

insert into note (id, note, created_by) values (1, 'Hello', 10);
insert into note (id, note, created_by) values (2, 'Lorem', 20);
insert into note (id, note, created_by) values (3, 'World', 10);
insert into note (id, note, created_by) values (4, 'Ipsum', 20);
insert into note (id, note, created_by) values (5, 'Chicago', 30);

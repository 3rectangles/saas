alter table interview
add column if not exists version int;

update interview
set version = 0;

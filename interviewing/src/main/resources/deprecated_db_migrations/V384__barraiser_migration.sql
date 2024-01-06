alter table evaluation
add column if not exists version int;

update evaluation
set version = 0;

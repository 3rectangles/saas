create table if not exists skill (
    id serial primary key,
    name text,
    domain text,
    parent int,
    created_on timestamp,
    updated_on timestamp
);

alter table candidate_process alter column id type text;
alter table cart_item alter column id type text;
alter table company alter column id type text;
alter table domain alter column id type text;
alter table interview_structure alter column id type text;
alter table payment alter column id type text;

alter table skill alter column id type text;
drop table skills;




drop table skill;
create table skill (
    id text primary key,
    name text,
    parent_skill_id text
);

drop table interview_structure;

create table interview_structure (
    id text primary key,
    name text,
    domain_id text,
    min_experience int,
    max_experience int
);
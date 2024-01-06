create table skill_weightage (
    id text primary key,
    job_role_id text,
    skill_id text,
    weightage decimal,
    created_on timestamp,
    updated_on timestamp
);

create table job_role (
    id text primary key,
    name text,
    domain_id text,
    company_id text,
    category text,
    min_exp int,
    max_exp int,
    created_on timestamp,
    updated_on timestamp
);

alter table interview
 add column job_role_id text
 ;


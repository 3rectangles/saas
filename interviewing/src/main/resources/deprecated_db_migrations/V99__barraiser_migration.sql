drop table if exists expert;

create table if not exists expert(
    id varchar(100) primary key,
    cost decimal,
    currency text,
    is_active bool,
    ops_rep text,
    created_on timestamp,
    updated_on timestamp
);

drop table if exists user_domain;
drop table if exists user_skill;

create table if not exists user_domain(
    id varchar(100) primary key,
    user_id text,
    domain_id text,
    type text,
    created_on timestamp,
    updated_on timestamp
);

create table if not exists user_skill(
    id varchar(100) primary key,
    user_id text,
    skill_id text,
    created_on timestamp,
    updated_on timestamp
);

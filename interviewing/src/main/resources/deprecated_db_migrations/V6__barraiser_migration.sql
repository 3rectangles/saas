create table if not exists skill (
    id serial primary key,
    name text,
    domain text,
    parent text,
    created_on timestamp,
    updated_on timestamp
);




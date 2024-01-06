create table if not exists status(
    id text primary key,
    internal_status text,
    entity_type text,
    display_status text,
    ranking int,
    created_on timestamp,
    updated_on timestamp
);

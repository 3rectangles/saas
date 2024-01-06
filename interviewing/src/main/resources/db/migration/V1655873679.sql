create table if not exists user_details_snapshot (
    id text primary key,
    entity_id text,
    entity_type text,
    payload jsonb,
    created_on timestamp,
    updated_on timestamp
);

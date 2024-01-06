create table if not exists calendar_entity (
    id text primary key,
    entity_id text,
    entity_type text,
    event_id text,
    account_id text,
    status text,
    created_on timestamp,
    updated_on timestamp
)


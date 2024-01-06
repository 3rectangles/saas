create table if not exists communication_log (
    id text primary key,
    event_type text,
    recipient_type text,
    entity_id text,
    entity_type text,
    partner_id text,
    channel text,
    status text,
    payload jsonb,
    created_on timestamp,
    updated_on timestamp
);

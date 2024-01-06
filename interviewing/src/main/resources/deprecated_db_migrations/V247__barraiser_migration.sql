create table if not exists channel_configuration(
    id text primary key,
    target_entity_id text,
    target_entity_type text,
    recipient text,
    recipient_id text,
    channel_type text,
    secrets text,
    disabled_on timestamp,
    created_on timestamp,
    updated_on timestamp
)

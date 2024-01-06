create table if not exists partner_configurations
(
    id serial primary key,
    partner_id text,
    config jsonb,
    created_by text,
    created_on timestamp,
    updated_on timestamp,
    deleted_on timestamp
)

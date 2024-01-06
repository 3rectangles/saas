create table if not exists slack_event_info(
    id text primary key,
    event_type text,
    template text,
    partner_id text,
    config_id text,
    disabled_on timestamp,
    created_on timestamp,
    updated_on timestamp
)

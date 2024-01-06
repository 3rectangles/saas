create table if not exists blacklist_reason (
    id text primary key,
    reason text,
    category text,
    default_blacklist_period_in_days integer,
    created_on timestamp,
    updated_on timestamp
)


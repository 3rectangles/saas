create table if not exists user_blacklist (
    id text primary key,
    user_id text,
    user_type text,
    granularity text,
    reason_id text,
    company_id text,
    blacklist_start_date timestamp,
    blacklist_end_date timestamp,
    created_on timestamp,
    updated_on timestamp,
    created_by text
)

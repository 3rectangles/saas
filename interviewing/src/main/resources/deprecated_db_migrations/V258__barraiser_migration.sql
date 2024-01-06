create table if not exists evaluation_access_filter (
    id text primary key,
    user_id text,
    partner_id text,
    filter jsonb,
    created_on timestamp,
    updated_on timestamp
);

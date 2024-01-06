create table if not exists api_key (
    id text primary key,
    key text,
    key_name text,
    scope text,
    roles text[],
    user_id text,
    partner_id text,
    created_on timestamp,
    disabled_on timestamp,
    updated_on timestamp,
    UNIQUE(key_name)
);

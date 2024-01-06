create table if not exists user_whitelist(
    id text primary key,
    user_id text,
    user_type text,
    partner_company_id text,
    whitelist_start_date timestamp,
    whitelist_end_date timestamp,
    created_on timestamp,
    updated_on timestamp,
    created_by text
);

alter table user_blacklist
rename company_id to partner_company_id;

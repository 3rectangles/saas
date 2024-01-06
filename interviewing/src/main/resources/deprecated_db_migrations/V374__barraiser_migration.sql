create table if not exists partner_blacklisted_domain(
    id text primary key,
    partner_id text,
    email_domain text,
    created_on timestamp,
    updated_on timestamp
);


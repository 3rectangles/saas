create table if not exists partner_reps(
    id text primary key,
    partner_id text,
    disabled_on timestamp,
    created_on timestamp,
    updated_on timestamp
);

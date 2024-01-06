create table if not exists event_to_entity (
    id text primary key,
    event_type text,
    entity_type text,
    entity_id_path text[]
);

create table if not exists communication_template_config (
    id text primary key,
    event_type text,
    channel text,
    recipient_type text,
    partner_id text,
    template_rule text,
    enabled boolean,
    created_on timestamp,
    updated_on timestamp
);

create table if not exists email_template (
    id text primary key,
    subject text,
    body text,
    query text,
    created_on timestamp,
    updated_on timestamp
);

alter table email_template
add column if not exists header text;

create table if not exists user_communication_subscription (
    id text primary key,
    user_id text,
    event_type text,
    subscription_rule text,
    created_on timestamp,
    updated_on timestamp
);

alter table email_template
add column if not exists branding text;

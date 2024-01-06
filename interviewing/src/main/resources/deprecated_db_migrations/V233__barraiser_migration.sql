alter table status
add column partner_id text;

create table if not exists status_order(
    id text primary key,
    partner_id text,
    status_id text,
    order_index int,
    created_on timestamp,
    updated_on timestamp
);

alter table evaluation
add column partner_status_id text;

alter table status
add constraint internal_status_partner_id_unique unique(internal_status, partner_id);

create table if not exists evaluation_change_history(
    id text primary key,
    evaluation_id text,
    field_name text,
    field_value text,
    created_by text,
    created_on timestamp,
    updated_on timestamp
);

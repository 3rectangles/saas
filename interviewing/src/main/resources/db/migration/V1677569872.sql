alter table partner_company
add column partnership_model_id text;

create table IF NOT EXISTS partnership_model(
    id text primary key,
    model_name text,
    partnership_type text,
    enabled_features text[],
    base_config jsonb,
    description text,
    created_on timestamp,
	updated_on timestamp
);

create table IF NOT EXISTS customer_configuration (
    id text primary key,
    partner_id text,
    entity_type text,
    entity_id text,
    config jsonb,
    created_by text,
    deleted_on timestamp,
    created_on timestamp,
	updated_on timestamp
);





drop table IF EXISTS pricing.partner_config;

drop table IF EXISTS pricing.contractual_pricing_config;

drop table IF EXISTS pricing.demo_pricing;

drop index IF EXISTS demo_pricing_index;


create table IF NOT EXISTS pricing.partner_config(
    id text primary key,
    partner_id text,
    stage text,
    number_of_interviews_for_demo integer,
    created_by text,
    applicable_from timestamp,
    applicable_till timestamp,
    created_on timestamp,
	updated_on timestamp
);

create table IF NOT EXISTS pricing.contractual_pricing_config(
    id text primary key,
    partner_id text,
    pricing_type text,
    price jsonb,
    default_margin numeric,
    applicable_from timestamp,
    applicable_till timestamp,
    created_by text,
    deprecated_on timestamp,
    should_be_considered_for_billing bool,
    created_on timestamp,
	updated_on timestamp
);

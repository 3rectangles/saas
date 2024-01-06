create schema IF NOT EXISTS pricing;

create table IF NOT EXISTS pricing.partner_config(
    id text primary key,
    partner_id text,
    stage text,
    default_margin numeric,
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
    applicable_from timestamp,
    applicable_till timestamp,
    created_by text,
    deprecated_on timestamp,
    created_on timestamp,
	updated_on timestamp
);

create table IF NOT EXISTS pricing.work_experience_based_pricing(
    id text primary key,
    partner_id text,
    round_type text,
    work_experience_lower_bound int,
    work_experience_upper_bound int,
    price jsonb,
    applicable_from timestamp,
    applicable_till timestamp,
    created_by text,
    deprecated_on timestamp,
    created_on timestamp,
	updated_on timestamp
);

create table IF NOT EXISTS pricing.job_role_based_pricing(
    id text primary key,
    job_role_id text,
    interview_structure_id text,
    price jsonb,
    margin numeric,
    applicable_from timestamp,
    applicable_till timestamp,
    created_by text,
    created_on timestamp,
	updated_on timestamp
);

create table IF NOT EXISTS pricing.demo_pricing(
    id text primary key,
    partner_id text,
    pricing_type text,
    price jsonb,
    number_of_interviews integer,
    applicable_from timestamp,
    applicable_till timestamp,
    created_by text,
    created_on timestamp,
	updated_on timestamp
);

CREATE INDEX IF NOT EXISTS partner_config_partner_id_index ON pricing.partner_config
(
    partner_id
);

CREATE INDEX IF NOT EXISTS contractual_pricing_config_partner_id_index ON pricing.contractual_pricing_config
(
    partner_id
);

CREATE INDEX IF NOT EXISTS work_experience_based_pricing_index ON pricing.work_experience_based_pricing
(
    partner_id, round_type, work_experience_lower_bound
);

CREATE INDEX IF NOT EXISTS demo_pricing_index ON pricing.demo_pricing
(
    partner_id, pricing_type
);

CREATE INDEX IF NOT EXISTS job_role_based_pricing_index ON pricing.job_role_based_pricing
(
    job_role_id, interview_structure_id
);

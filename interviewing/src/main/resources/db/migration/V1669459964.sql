create schema IF NOT EXISTS authz;

CREATE TABLE IF NOT EXISTS authz.permission(
    id text primary key,
    display_name text,
    action text,
    resource text,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS authz.role(
    id text primary key,
    partner_id text,
    name text,
    display_name text,
    type text,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS authz.role_to_permission_mapping(
    id text primary key,
    partner_id text,
    role_id text,
    permission_id text,
    condition jsonb,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS authz.user_to_role_mapping(
    id text primary key,
    user_id text,
    role_id text,
    authorization_dimension text,
    authorization_dimension_value text,
    created_on timestamp,
    updated_on timestamp
);


ALTER TABLE partner_reps
ADD COLUMN IF NOT EXISTS location text,
ADD COLUMN IF NOT EXISTS team text;

ALTER TABLE authz.user_to_role_mapping
ADD COLUMN IF NOT EXISTS deleted_on timestamp,
ADD COLUMN IF NOT EXISTS updated_by text;















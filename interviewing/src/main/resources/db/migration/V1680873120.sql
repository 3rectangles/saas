ALTER TABLE job_role add column if not exists locations text[];
ALTER TABLE job_role add column if not exists teams text[];
ALTER TABLE job_role add column if not exists br_status text;
ALTER TABLE job_role add column if not exists ats_status text;
ALTER TABLE job_role add column if not exists hiring_managers text[];
ALTER TABLE job_role add column if not exists recruiters text[];
ALTER TABLE job_role add column if not exists hiring_team_members text[];
ALTER TABLE job_role add column if not exists creation_source text;
ALTER TABLE job_role add column if not exists creation_meta text;


create table if not exists team(
    id text primary key,
    name text,
    description text,
    ats_id text,
    creation_source text,
    creation_source_meta text,
    created_on timestamp,
	updated_on timestamp
);

create table if not exists location (
    id text primary key,
    name text,
    description text,
    ats_id text,
    creation_source text,
    creation_source_meta text,
    created_on timestamp,
	updated_on timestamp
);

create table if not exists ats_partner_reps_mapping(
    id text primary key,
    partner_id text,
    ats_provider text,
    br_partner_rep_id text,
    ats_partner_rep_id text,
    ats_user_name text,
    created_on timestamp,
	updated_on timestamp
);

alter table partner_reps
add column if not exists creation_source text;

alter table partner_reps
add column if not exists creation_source_meta text;

ALTER TABLE ats_user_role_mapping add column if not exists ats_user_role_name text;

ALTER TABLE ats_partner_reps_mapping
ADD CONSTRAINT unique_ats_partner_rep_per_partner
UNIQUE(ats_partner_rep_id,partner_id);


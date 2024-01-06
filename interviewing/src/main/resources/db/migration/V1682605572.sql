ALTER TABLE job_role add column if not exists ext_full_sync bool;

CREATE TABLE IF NOT EXISTS external_api_call_timestamp (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	api_name varchar(255) NULL,
	call_timestamp timestamp NULL,
	"type" varchar(255) NULL,
	CONSTRAINT external_api_call_timestamp_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS INT_LOCK  (
	LOCK_KEY CHAR(36) NOT NULL,
	REGION VARCHAR(100) NOT NULL,
	CLIENT_ID CHAR(36),
	CREATED_DATE TIMESTAMP NOT NULL,
	constraint INT_LOCK_PK primary key (LOCK_KEY, REGION)
);


CREATE TABLE IF NOT EXISTS stage_departments (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	"data" jsonb NULL,
	expired_on timestamp NULL,
	"type" varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT stage_departments_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS stage_job_role_stages (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	"data" jsonb NULL,
	expired_on timestamp NULL,
	"type" varchar(255) NULL,
	job_id varchar(255) NULL,
	"name" varchar(255) NULL,
	job_role_internal_identifier varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT stage_job_role_stages_pkey PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS stage_job_roles (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	"data" jsonb NULL,
	expired_on timestamp NULL,
	"type" varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT stage_job_roles_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS stage_mini_job_roles (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	"data" jsonb NULL,
	expired_on timestamp NULL,
	"type" varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT stage_mini_job_roles_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS stage_offices (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	"data" jsonb NULL,
	expired_on timestamp NULL,
	"type" varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT stage_offices_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS stage_user_details (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	"data" jsonb NULL,
	expired_on timestamp NULL,
	"type" varchar(255) NULL,
	role_only bool NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT stage_user_details_pkey PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS merge_fetched_data (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	"type" varchar(255) NULL,
	uuid varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL
);


CREATE TABLE IF NOT EXISTS merge_flatten_departments (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	external_identifier varchar(255) NULL,
	remote_id varchar(255) NULL,
	"name" varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT merge_flatten_departments_pkey PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS merge_flatten_job_role_stages (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	external_identifier varchar(255) NULL,
	remote_id varchar(255) NULL,
	job_id varchar(255) NULL,
	"name" varchar(255) NULL,
	job_role_internal_identifier varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT merge_flatten_job_role_stages_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS merge_flatten_job_roles (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	external_identifier varchar(255) NULL,
	remote_id varchar(255) NULL,
	department_ids TEXT NULL,
	job_role_status varchar(255) NULL,
	"name" varchar(255) NULL,
	office_ids varchar(255) NULL,
	hiring_managers_ids varchar(255) NULL,
	recruiter_ids varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT merge_flatten_job_roles_pkey PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS merge_flatten_mini_job_roles (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	external_identifier varchar(255) NULL,
	remote_id varchar(255) NULL,
	department_ids varchar(255) NULL,
	job_role_status varchar(255) NULL,
	"name" varchar(255) NULL,
	office_ids varchar(255) NULL,
	hiring_managers_ids varchar(255) NULL,
	recruiter_ids varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT merge_flatten_mini_job_roles_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS merge_flatten_offices (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	external_identifier varchar(255) NULL,
	remote_id varchar(255) NULL,
	"location" varchar(255) NULL,
	"name" varchar(255) NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT merge_flatten_offices_pkey PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS merge_flatten_user_details (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	identifier varchar(255) NULL,
	partner_id varchar(255) NULL,
	process_id varchar(255) NULL,
	status varchar(255) NULL,
	external_identifier varchar(255) NULL,
	remote_id varchar(255) NULL,
	access_role varchar(255) NULL,
	email varchar(255) NULL,
	first_name varchar(255) NULL,
	last_name varchar(255) NULL,
	phone_number varchar(255) NULL,
	role_only bool NULL,
	error_code varchar(255) NULL,
	error_message varchar(255) NULL,
	CONSTRAINT merge_flatten_user_details_pkey PRIMARY KEY (id)
);


alter table merge_flatten_job_roles
ALTER COLUMN department_ids TYPE TEXT USING department_ids::TEXT;
alter table merge_flatten_job_roles
ALTER COLUMN office_ids TYPE TEXT USING department_ids::TEXT;
alter table merge_flatten_job_roles
ALTER COLUMN recruiter_ids TYPE TEXT USING department_ids::TEXT;
alter table merge_flatten_job_roles
ALTER COLUMN hiring_managers_ids TYPE TEXT USING department_ids::TEXT;
alter table  stage_departments 
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  stage_job_role_stages  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  stage_job_roles  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  stage_mini_job_roles  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  stage_offices  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  stage_user_details  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  merge_flatten_departments  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  merge_flatten_job_role_stages  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  merge_flatten_job_roles  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  merge_flatten_mini_job_roles  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  merge_flatten_offices  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;
alter table  merge_flatten_user_details  
ALTER COLUMN error_message TYPE TEXT USING error_message::TEXT;

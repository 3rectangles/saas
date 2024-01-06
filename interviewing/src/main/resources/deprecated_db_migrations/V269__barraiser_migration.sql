create table if not exists evaluation_search (
	id text primary key,
	company_id text,
	deleted_on bigint,
	poc_email text,
	job_role_id text,
	domain_id text,
	candidate_name text,
	display_status text,
	created_on timestamp,
	status_updated_on timestamp
);

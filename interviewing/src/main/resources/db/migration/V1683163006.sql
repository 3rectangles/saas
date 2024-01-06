ALTER TABLE public.job_role ADD IF NOT EXISTS active_candidates_count int4 NULL;

CREATE TABLE IF NOT EXISTS filter (
	id text NULL,
	query text NULL,
	dependant_fields _text NULL,
	filter_context text NULL,
	field_type text NULL,
	display_name text NULL,
	name text NULL,
	operations_possible _text NULL,
	filter_type text NULL,
	entity_type text NULL,
    query_mapping text NULL,
    internal_name text NULL,
    default_value text NULL,
	created_on timestamp NULL,
	updated_on timestamp NULL
);

ALTER TABLE job_role
ALTER COLUMN br_status TYPE text[]
USING ARRAY[br_status];

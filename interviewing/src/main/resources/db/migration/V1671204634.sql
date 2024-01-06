CREATE TABLE public.training_snippet (
	id text NOT NULL,
	user_id text NOT NULL,
	partner_id text NOT NULL,
	title text NOT NULL,
	description text,
	start_time int4 NOT NULL,
	end_time int4 NOT NULL,
	video_id text NOT NULL,
	video_url text NOT NULL,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	CONSTRAINT training_snippet_pkey PRIMARY KEY (id)
);


CREATE TABLE public.training_tag (
	id text NOT NULL,
	user_id text NOT NULL,
	partner_id text NOT NULL,
	name text NOT NULL,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	CONSTRAINT training_tag_pkey PRIMARY KEY (id)
);

CREATE TABLE public.training_tag_mapping (
	id text NOT NULL,
	tag_id text NULL,
	training_snippet_id text NULL,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	CONSTRAINT training_tag_mapping_pkey PRIMARY KEY (id)
);

CREATE TABLE public.training_job_role_mapping (
	id text NOT NULL,
	job_role_id text NULL,
	training_snippet_id text NULL,
	created_on timestamp NULL,
	updated_on timestamp NULL,
	CONSTRAINT training_job_role_mapping_pkey PRIMARY KEY (id)
);


create table if not exists bgs_enquiry (
    id text  primary key,
    interview_id text,
    cost decimal,
    interested boolean,
    created_on timestamp,
	updated_on timestamp
);

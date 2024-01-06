alter table job_role
add column if not exists default_poc_email text;

create table if not exists greenhouse_evaluation (
    id text primary key,
    evaluation_id text,
    profile_url text,
    update_status_url text,
    created_on timestamp,
    updated_on timestamp
);

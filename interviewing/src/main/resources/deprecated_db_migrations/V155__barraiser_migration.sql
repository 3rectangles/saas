create table job_role_history (
    id text primary key,
    job_role_id text,
    job_role_raw_state jsonb,
    created_on timestamp,
    updated_on timestamp
);

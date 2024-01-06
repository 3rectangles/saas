create table if not exists job_role_to_interview_structure (
    id text primary key,
    job_role_id text,
    interview_round text,
    interview_structure_id text
);
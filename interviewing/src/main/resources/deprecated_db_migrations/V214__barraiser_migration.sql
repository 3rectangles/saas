alter table if exists interview
add column interview_structure_id text;

alter table if exists job_role_to_interview_structure
add column order_index int;

ALTER TABLE job_role_to_interview_structure
DROP CONSTRAINT if exists unique_interview_str_per_job_role_per_round;

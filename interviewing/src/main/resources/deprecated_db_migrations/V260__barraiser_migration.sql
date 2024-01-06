alter table interview_structure
add column if not exists cutoff_score int,
add column if not exists threshold_score int,
add column if not exists requires_approval bool;

alter table job_role
add column cutoff_score int;

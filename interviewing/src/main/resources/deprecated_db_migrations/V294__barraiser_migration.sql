alter table job_role_to_interview_structure
add column if not exists cutoff_score int,
add column if not exists threshold_score int,
add column if not exists requires_approval bool;

alter table interview_structure
drop column cutoff_score,
drop column threshold_score,
drop column requires_approval;

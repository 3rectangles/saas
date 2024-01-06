alter table candidate_process
    add column created_on timestamp,
    add column updated_on timestamp;

alter table domain
    add column created_on timestamp,
    add column updated_on timestamp;

alter table interview_structure
    add column created_on timestamp,
    add column updated_on timestamp;

alter table interview_structure_skills
    add column created_on timestamp,
    add column updated_on timestamp;

alter table overall_feedback
    add column created_on timestamp,
    add column updated_on timestamp;

alter table question
    add column created_on timestamp,
    add column updated_on timestamp;

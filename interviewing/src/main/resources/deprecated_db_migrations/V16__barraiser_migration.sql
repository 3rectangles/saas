alter table skill
    add column domain_id text;


alter table interview
    add column interview_structure_link text,
    add column feedback_link text,
    add column final_submission_link text;

alter table role
    add column domain_id text;

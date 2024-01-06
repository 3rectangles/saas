alter table interview_structure
add column interview_flow_link text;

alter table interviewing_note
add column interview_flow jsonb;

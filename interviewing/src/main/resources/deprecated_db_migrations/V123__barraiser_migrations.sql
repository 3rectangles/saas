
alter table interview_structure_excerpts
add column if not exists created_on timestamp,
add column if not exists updated_on timestamp;


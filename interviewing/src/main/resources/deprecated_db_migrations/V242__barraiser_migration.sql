alter table interview_structure_skills
add column is_specific bool,
add column is_optional bool;

alter table interview_structure
add column all_skills_found bool;

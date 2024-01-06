alter table excerpt
add column if not exists depth text;
alter table interview_structure_excerpts
add column if not exists company_specific text;
alter table default_questions
add column if not exists excerpt_id text;


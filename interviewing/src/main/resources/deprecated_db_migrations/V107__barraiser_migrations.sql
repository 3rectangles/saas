create table if not exists default_questions (
    id text  primary key,
    interview_structure_id text,
    question text,
    created_on timestamp,
	updated_on timestamp
);

ALTER TABLE question
ADD COLUMN is_default bool;
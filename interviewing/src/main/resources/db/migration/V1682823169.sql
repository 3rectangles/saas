CREATE TABLE IF NOT EXISTS fixed_question_expert (
    id text NOT NULL,
    question_id text,
    expert_id text,
	created_on timestamp NOT NULL DEFAULT NOW(),
	updated_on timestamp NOT NULL DEFAULT NOW(),
	valid_flag bool DEFAULT TRUE,
	CONSTRAINT question_expert_id PRIMARY KEY (question_id, expert_id)
);

CREATE TABLE IF NOT EXISTS fixed_questions (
    id text NOT NULL PRIMARY KEY,
    question text,
	solution text,
    added_by_user_id text,
	created_on timestamp NOT NULL DEFAULT NOW(),
	updated_on timestamp NOT NULL DEFAULT NOW(),
	valid_flag bool DEFAULT TRUE
);
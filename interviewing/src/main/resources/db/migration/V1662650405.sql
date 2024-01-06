ALTER TABLE evaluation
ADD COLUMN IF NOT EXISTS candidate_id text;


CREATE TABLE IF NOT EXISTS candidate(
    id text primary key,
    user_id text,
    first_name text,
    last_name text,
    initials text,
    alma_mater text,
    birth_date text,
    category text,
    country text,
    designation text,
    linked_in_profile text,
    work_experience_in_months integer,
    current_ctc text,
    resume_url text,
    resume_id text,
    redacted_resume_url text,
    current_company_id text,
    current_company_name text,
    last_Companies text[],
    achievements text[],
    timezone text,
    created_on timestamp,
    updated_on timestamp
);

CREATE INDEX candidate_user_id_idx ON candidate(user_id);

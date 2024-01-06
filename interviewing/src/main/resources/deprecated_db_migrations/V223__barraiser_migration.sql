ALTER TABLE evaluation_score
add column process_type text;

create table interview_score (
    id text primary key,
    interview_id text,
    skill_id text,
    score decimal,
    weightage decimal,
    scoring_algo_version text,
    created_on timestamp,
    updated_on timestamp
);

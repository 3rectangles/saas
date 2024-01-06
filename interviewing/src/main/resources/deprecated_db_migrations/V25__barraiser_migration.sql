alter table interview
    add column evaluation_id text;

create table evaluation (
    id text primary key,
    user_id text,
    interview_structure text,
    status text,
    source text,
    created_on timestamp,
    updated_on timestamp
);

create table evaluation_score (
    id text primary key,
    evaluation_id text,
    skill_id text,
    score decimal,
    weightage decimal,
    scoring_algo_version int
);

create table transcript (
    id text primary key,
     interview_id text,
     start_time int,
     end_time int,
     speaker text,
     content text,
     tagged_question text
);

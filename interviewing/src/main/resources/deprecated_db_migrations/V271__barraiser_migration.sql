create table if not exists candidate_availability (
    id text primary key,
    user_id text,
    interview_id text,
    start_date bigint,
    end_date bigint,
    created_on timestamp,
    updated_on timestamp
);

create table if not exists interview_to_eligible_experts (
    id text primary key,
    interview_id text,
    interviewer_id text,
    created_on timestamp,
    updated_on timestamp
);

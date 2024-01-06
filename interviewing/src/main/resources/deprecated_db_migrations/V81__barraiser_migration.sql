create table if not exists interview_confirmation(
    id                       text primary key,
    interview_id             text,
    candidate_confirmation   bool,
    interviewer_confirmation bool
);
create table if not exists interviewer_recommendation
(
    id text primary key,
    interview_id text,
    hiring_rating int,
    remarks text,
    cheating_suspected_remarks text,
    interview_incomplete_remarks text,
    created_on timestamp,
    updated_on timestamp
)

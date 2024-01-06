create table interviewee_feedback (
    id text primary key,
    created_on timestamp,
    updated_on timestamp ,
    interview_id text,
    parameter text,
    rating int ,
    remarks text
);
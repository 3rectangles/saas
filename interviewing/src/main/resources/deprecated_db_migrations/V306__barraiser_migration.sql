drop table interview_pad;
create table if not exists interview_pad (
    id text PRIMARY KEY,
    interview_id text UNIQUE,
    interviewer_pad text,
    interviewee_pad text,
    created_on timestamp,
    updated_on timestamp
);


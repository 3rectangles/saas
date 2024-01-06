create table if not exists webex_meeting(
    id text primary key,
    interview_id text,
    reschedule_count int,
    meeting_number text,
    join_link text,
    password text,
    start_date int,
    end_date int,
    created_on timestamp,
    updated_on timestamp
);

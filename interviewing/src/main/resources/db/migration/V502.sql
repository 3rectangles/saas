create table if not exists dyte_meeting (
    meeting_id text primary key,
    interview_id text,
    reschedule_count integer,
    room_name text,
    created_on timestamp,
    updated_on timestamp
);


create table if not exists dyte_participant (
    id text primary key,
    meeting_id text,
    participant_id text,
    participant_meeting_role text,
    auth_token text,
    created_on timestamp,
    updated_on timestamp
);


create table if not exists interviewing_note(
    id text primary key,
    interview_id text,
    notes jsonb,
    created_on timestamp,
    updated_on timestamp
);


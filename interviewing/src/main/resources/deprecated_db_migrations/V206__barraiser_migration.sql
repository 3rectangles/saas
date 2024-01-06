create table interview_change_history (
    id text primary key,
    interview_id text,
    field_name text,
    field_value text,
    created_on timestamp,
    created_by text
);

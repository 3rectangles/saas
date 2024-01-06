create table if not exists recurrence_slot(
    id text primary key,
    user_id text,
    start_date bigint,
    end_date bigint,
    recurrence_rule text[],
    slot_type text,
    timezone text,
    source text,
    properties jsonb,
    calendar_event_id text,
    calendar_email text,
    deleted_on timestamp,
    created_on timestamp,
    updated_on timestamp
);

create table if not exists exception_slot(
    id text primary key,
    user_id text,
    start_date bigint,
    end_date bigint,
    recurrence_id text,
    original_start_date bigint,
    slot_type text,
    timezone text,
    source text,
    properties jsonb,
    calendar_event_id text,
    calendar_email text,
    cancelled_on timestamp,
    deleted_on timestamp,
    created_on timestamp,
    updated_on timestamp
);

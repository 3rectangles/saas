create table if not exists scheduling_session(
    id text primary key,
    interview_id text,
    reschedule_count integer,
    interview_cost jsonb,
    margin numeric,
    created_on timestamp,
	updated_on timestamp
);

CREATE INDEX IF NOT EXISTS scheduling_session_interview_reschedule_count ON scheduling_session
(
    interview_id, reschedule_count
);

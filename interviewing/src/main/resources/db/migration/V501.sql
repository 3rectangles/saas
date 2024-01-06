CREATE TABLE IF NOT EXISTS overall_feedback_suggestion (
    id text PRIMARY KEY,
    interview_id text,
    suggestion text,
    type text,
    request jsonb,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS data_science_user_activity (
    id text PRIMARY KEY,
    entity_type text,
    context text,
    payload text,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS feedback_recommendation (
    id text PRIMARY KEY,
    interview_id text,
    feedback_id text,
    recommendation text,
    request jsonb,
    created_on timestamp,
    updated_on timestamp
);

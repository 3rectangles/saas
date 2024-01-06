CREATE TABLE relaxed_meeting_interception_config (
    id SERIAL PRIMARY KEY,
    partner_id TEXT,
    created_on TIMESTAMP,
    updated_on TIMESTAMP,
    keyword TEXT[]
);
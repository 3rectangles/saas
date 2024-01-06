CREATE TABLE IF NOT EXISTS ats_user_role_mapping (
    id TEXT PRIMARY KEY,
    partner_id TEXT,
    ats_provider TEXT,
    ats_user_role_id TEXT,
    br_user_role_id TEXT,
    created_on TIMESTAMP,
    updated_on TIMESTAMP
);

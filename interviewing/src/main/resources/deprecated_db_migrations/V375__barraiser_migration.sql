CREATE TABLE IF NOT EXISTS ats_access_token (
    id TEXT PRIMARY KEY,
    partner_id text,
    token text,
    token_type text,
    ats_provider text,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS ats_to_br_evaluation(
    id TEXT PRIMARY KEY,
    br_evaluation_id TEXT,
    partner_id TEXT,
    ats_evaluation_id TEXT,
    ats_provider TEXT,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS ats_job_posting_to_br_job_role (
    id TEXT PRIMARY KEY,
    partner_id TEXT,
    br_job_role_id TEXT,
    ats_job_posting_id TEXT,
	ats_provider TEXT,
    created_on TIMESTAMP,
    updated_on TIMESTAMP
);

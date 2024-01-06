CREATE TABLE IF NOT EXISTS ats_interview_to_candidate_mapping (
    id TEXT PRIMARY KEY,
    partner_id TEXT,
    ats_interview_id TEXT,
    ats_candidate_id TEXT,
	ats_provider TEXT,
    created_on TIMESTAMP,
    updated_on TIMESTAMP
);

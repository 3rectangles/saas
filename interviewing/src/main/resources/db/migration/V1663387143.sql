CREATE TABLE IF NOT EXISTS ats_interview_structure_mapping (
    id TEXT PRIMARY KEY,
    ats_provider TEXT,
    br_interview_structure_id TEXT,
    ats_interview_structure_id TEXT,
    created_on TIMESTAMP,
    updated_on TIMESTAMP
);

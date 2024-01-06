ALTER TABLE location ADD COLUMN IF NOT EXISTS partner_id text;
ALTER TABLE location ADD COLUMN IF NOT EXISTS ats_provider text;

ALTER TABLE team ADD COLUMN IF NOT EXISTS partner_id text;
ALTER TABLE team ADD COLUMN IF NOT EXISTS ats_provider text;

ALTER TABLE job_role
ALTER COLUMN br_status TYPE text[]
USING ARRAY[br_status];

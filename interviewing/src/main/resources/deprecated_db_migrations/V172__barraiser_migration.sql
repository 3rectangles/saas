ALTER TABLE job_role
ADD COLUMN IF NOT EXISTS deprecated bool,
ADD COLUMN IF NOT EXISTS deprecated_on timestamp,
ADD COLUMN IF NOT EXISTS deleted_on timestamp;

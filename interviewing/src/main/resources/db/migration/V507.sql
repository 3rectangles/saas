ALTER TABLE ats_credential ADD COLUMN IF NOT EXISTS encrypted_token BYTEA;

ALTER TABLE ats_access_token ADD COLUMN IF NOT EXISTS encrypted_token BYTEA;

ALTER TABLE api_key ADD COLUMN IF NOT EXISTS encrypted_key BYTEA;

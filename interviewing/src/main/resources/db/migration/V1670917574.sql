ALTER TABLE partner_reps
RENAME COLUMN location to locations;

ALTER TABLE partner_reps
RENAME COLUMN team to teams;

ALTER TABLE partner_reps
ADD COLUMN IF NOT EXISTS partner_roles text[];



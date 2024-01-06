ALTER TABLE partner_reps
ADD COLUMN partner_rep_id text;

UPDATE partner_reps
set partner_rep_id = id;

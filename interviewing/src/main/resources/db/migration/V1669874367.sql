ALTER TABLE evaluation_access_filter DROP CONSTRAINT IF EXISTS unique_filter_per_partner;

ALTER TABLE evaluation_access_filter
ADD CONSTRAINT unique_filter_per_partner
UNIQUE(user_id,partner_id);

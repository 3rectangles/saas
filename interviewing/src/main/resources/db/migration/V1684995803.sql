ALTER TABLE partner_company ADD COLUMN if not exists scale_bgs int;
ALTER TABLE partner_company ADD COLUMN if not exists scale_scoring int;
ALTER TABLE partner_company ADD COLUMN if not exists overall_text_format int;
ALTER TABLE evaluation_search ALTER COLUMN bgs TYPE FLOAT;

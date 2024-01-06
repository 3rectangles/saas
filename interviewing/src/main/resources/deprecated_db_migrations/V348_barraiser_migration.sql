ALTER TABLE api_key DROP CONSTRAINT api_key_key_name_key;

ALTER TABLE api_key ADD api_key_partner_id_key_name_unique_combination UNIQUE(key_name, partner_id);

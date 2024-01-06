ALTER TABLE user_details
DROP COLUMN redacted_resume_link,
ADD COLUMN redacted_resume_url text;


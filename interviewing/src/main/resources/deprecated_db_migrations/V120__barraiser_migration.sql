ALTER TABLE evaluation
ADD COLUMN poc_email text;

ALTER TABLE user_details
ADD COLUMN redacted_resume_link text;
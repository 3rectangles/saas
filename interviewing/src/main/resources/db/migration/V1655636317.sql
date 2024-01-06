CREATE TABLE IF NOT EXISTS user_login_activity(
id text primary key,
email_id text,
login_key_type text,
login_key text,
is_login_attempt_successful boolean,
created_on timestamp,
updated_on timestamp);


CREATE TABLE IF NOT EXISTS login_blacklist(
id text primary key,
email_id text,
ttl bigint,
created_on timestamp,
updated_on timestamp
);


CREATE INDEX email_login_result_time_idx on user_login_activity(email_id,is_login_attempt_successful,created_on);

CREATE INDEX email_ttl_idx on login_blacklist(email_id,ttl);

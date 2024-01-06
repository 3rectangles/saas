ALTER TABLE booked_slot
ADD COLUMN IF NOT EXISTS email VARCHAR(255) NULL,
ADD COLUMN IF NOT EXISTS google_cal_id varchar(255) NULL,
ADD COLUMN IF NOT EXISTS source varchar(255) NULL,
ADD COLUMN IF NOT EXISTS deleted_on timestamp NULL;

CREATE TABLE IF NOT EXISTS notification_channel_info (
    id text primary key,
    channel_expiry_time int8 NULL,
    channel_id varchar(255) NULL,
    email varchar(255) NULL,
    last_synced timestamp NULL,
    next_sync_token varchar(255) NULL,
    user_id varchar(255) NULL,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS token_info (
    id text primary key,
    created_on timestamp NULL,
    updated_on timestamp NULL,
    access_token varchar(255) NULL,
    deleted bool NOT NULL,
    email varchar(255) NULL,
    expired bool NOT NULL,
    expires_at timestamp NULL,
    provider varchar(255) NULL,
    refresh_token varchar(255) NULL,
    user_id varchar(255) NULL
);

CREATE INDEX IF NOT EXISTS access_token_index ON token_info USING btree (user_id, email, provider, deleted);

CREATE INDEX IF NOT EXISTS eventid_email_index ON booked_slot USING btree (email, google_cal_id);

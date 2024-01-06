CREATE TABLE IF NOT EXISTS slack_template (
    id text PRIMARY KEY,
    body text,
    query text,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS slack_recipient_configuration (
    id text PRIMARY KEY,
    partner_id text,
    event_type text,
    channel_configuration_ids text[],
    created_on timestamp,
    updated_on timestamp
);

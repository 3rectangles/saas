create table if not exists slack_integration(

    partner_id text,
    access_token text,
    channel_id text,
    team_id text,
    webhook_url text,
    created_on timestamp,
    updated_on timestamp
)

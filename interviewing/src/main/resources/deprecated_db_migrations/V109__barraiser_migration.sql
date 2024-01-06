
alter table interview
add column if not exists zoom_account_email text;

create table if not exists jira_workflow (
    id text primary key,
    from_state text,
    to_state text,
    transition text,
    action text,
    created_on timestamp,
    updated_on timestamp
);

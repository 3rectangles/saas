create table if not exists jira_workflow (
    id text primary key,
    from_state text,
    to_state text,
    transition text,
    action text,
    created_on timestamp,
    updated_on timestamp
);
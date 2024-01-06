create table if not exists evaluation_history(
    id text primary key,
    edit_id text,
    raw_evaluation jsonb,
    user_id text,
    created_on  timestamp
);
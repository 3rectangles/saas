alter table interview
add column if not exists redo_reason_id text;

alter table interview_history
add column if not exists redo_reason_id text;

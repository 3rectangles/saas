create table if not exists interview_process_quality(
    id text primary key,
    interview_id text,
    tagging_quality int
);
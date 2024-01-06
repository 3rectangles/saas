drop table if exists qc_feedback;

create table if not exists qc_comment (
   id text primary key,
   comment text,
   created_on timestamp,
   updated_on timestamp
);

alter table question
add column question_start_time bigint,
add column question_end_time bigint;
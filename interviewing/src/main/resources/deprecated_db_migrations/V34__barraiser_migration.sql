alter table question
add column irrelevant bool,
add column hands_on bool;

create table if not exists qc_feedback (
   id text primary key,
   category_id text,
   presentational_comment text,
   technical_comment text,
   created_on timestamp,
   updated_on timestamp
);
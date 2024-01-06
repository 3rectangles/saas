alter table qc_comment
drop column updated_time_epoch;

alter table qc_comment
add column comment_type text;
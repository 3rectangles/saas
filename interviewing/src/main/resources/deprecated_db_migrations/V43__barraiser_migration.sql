alter table question
drop column question_start_time,
drop column question_end_time,
add column start_time_epoch integer;


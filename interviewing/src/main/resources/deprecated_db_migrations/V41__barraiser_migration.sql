alter table interview
drop column interview_start,
drop column interview_end;


alter table qc_comment
add column commenterId text;
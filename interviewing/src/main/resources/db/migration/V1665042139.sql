alter table interview
add column if not exists meeting_link text;

update interview
set meeting_link = zoom_link;

alter table interview_history
add column if not exists meeting_link text;

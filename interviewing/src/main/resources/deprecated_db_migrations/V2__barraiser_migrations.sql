alter table interview
    add column domain_id int,
    add column zoom_link text,
    add column feedback_status text,
    add column question_tagging_status text,
    add column youtube_link text,
    add column video_link text,
    add column bgs_link text,
    add column rating decimal,
    add column duration decimal,
    add column interaction_percentage decimal,
    add column remarks text;


create table if not exists candidate_process(
   id serial primary key,
   user_id text,
   application_date int,
   bgs_submission_da int,
   bgs decimal,
   hiring_status text,
   company_id text
 );

create table interview_structure (
   id serial primary key,
   name text,
   domain_id text,
   sub_domain_id text,
   expreience_range text,
   categories json
   );

alter table skills
    add column parent integer;

alter table user_details
    add column resume_url text;
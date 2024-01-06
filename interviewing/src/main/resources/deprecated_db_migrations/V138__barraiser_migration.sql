create table if not exists interview_round_type_configuration (
   id text primary key,
   round_type text,
   company_id text,
   candidate_start_time_offset_mintues integer,
   candidate_end_time_offset_minutes integer,
   expert_start_time_offset_minutes integer,
   expert_end_time_offset_minutes integer,
   interview_scheduled_candidate_email_template text,
   interview_scheduled_expert_email_template text,
   description_template text,
   created_on timestamp,
   updated_on timestamp
);

alter table job_role_to_interview_structure
add column problem_statement_link text;


--peer round config
insert into interview_round_type_configuration(id,round_type,candidate_start_time_offset_mintues
,candidate_end_time_offset_minutes,expert_start_time_offset_minutes,expert_end_time_offset_minutes,
interview_scheduled_candidate_email_template,interview_scheduled_expert_email_template
)
values(uuid_generate_v4(),'PEER',0,60,0,60,'b2b_candidate_interview_scheduled','b2b_expert_interview_scheduled');

--expert round config
insert into interview_round_type_configuration(id,round_type,candidate_start_time_offset_mintues
,candidate_end_time_offset_minutes,expert_start_time_offset_minutes,expert_end_time_offset_minutes,
interview_scheduled_candidate_email_template,interview_scheduled_expert_email_template
)
values(uuid_generate_v4(),'EXPERT',0,60,0,60,'b2b_candidate_interview_scheduled','b2b_expert_interview_scheduled');

--machine round config
insert into interview_round_type_configuration(id,round_type,candidate_start_time_offset_mintues
,candidate_end_time_offset_minutes,expert_start_time_offset_minutes,expert_end_time_offset_minutes,
interview_scheduled_candidate_email_template,interview_scheduled_expert_email_template
)
values(uuid_generate_v4(),'MACHINE',0,120,45,120,'b2b_candidate_machine_interview_scheduled','b2b_expert_machine_interview_scheduled');

--machine2 round config
insert into interview_round_type_configuration(id,round_type,candidate_start_time_offset_mintues
,candidate_end_time_offset_minutes,expert_start_time_offset_minutes,expert_end_time_offset_minutes,
interview_scheduled_candidate_email_template,interview_scheduled_expert_email_template
)
values(uuid_generate_v4(),'MACHINE2',0,120,0,120,'b2b_candidate_machine2_interview_scheduled','b2b_expert_machine2_interview_scheduled');


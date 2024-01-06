alter table interview
add column if not exists ats_interview_feedback_link text;

alter table interview_structure
add column if not exists is_default bool;

alter table job_role
add column if not exists is_default bool;

alter table partner_company
add column if not exists default_interview_structure text;

alter table partner_company
add column if not exists use_ats_feedback bool;


insert into job_role(id,version_id,internal_display_name,candidate_display_name,is_default,created_on,updated_on)
values('07e0c01c-427c-11ee-be56-0242ac120002',0,'Default Job Role','Default Job Role',true,now(),now());

insert into interview_structure(id,name,interview_flow,is_default,created_on,updated_on)
values('4186d73d-c440-4048-9f39-7a9fdbd098a7','Default Interview Structure','{"version":"1"}',true,now(),now());

insert into job_role_to_interview_structure(id,job_role_id,job_role_version,interview_structure_id,order_index,interview_round,created_on,updated_on)
values(uuid_generate_v4(),'07e0c01c-427c-11ee-be56-0242ac120002',0,'4186d73d-c440-4048-9f39-7a9fdbd098a7',0,'INTERNAL',now(),now());





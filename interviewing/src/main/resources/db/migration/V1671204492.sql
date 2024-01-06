alter table ats_communication_template_config
rename column template to body_template;

alter table ats_communication_template_config
rename column replacement_template to body_replacement_template;

alter table ats_communication_template_config
add column if not exists subject_template text,
add column if not exists subject_replacement_template text;



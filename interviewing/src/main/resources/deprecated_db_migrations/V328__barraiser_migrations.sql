create table if not exists interview_structure_to_skill_interviewing_configuration(
      id text PRIMARY KEY,
      interview_structure_id text,
      skill_interviewing_configuration_id text,
      skill_interviewing_configuration_version integer

);

create table if not exists job_role_to_interview_structure_to_skill_conf(
    id text PRIMARY KEY,
    job_role_id text,
    job_role_version integer,
    interview_structure_id text,
    skill_interviewing_configuration_id text,
    skill_interviewing_configuration_version integer,
    role_specific_instructions text,
    round_index integer
);

create table if not exists rule (
     id text primary key,
     rule_body text,
     entity_type text
);

alter table default_question
    add column if not exists category_id text;


alter table default_question
    add column if not exists question_type text;

alter table default_question
    add column if not exists is_preinterview_question bool;

-- Adding jd document to job_role
alter table job_role
    add column if not exists jd_document_id text;

alter table job_role
    add column if not exists partner_id text;

-- Updating existing jobRoles for partner_id
update job_role
set partner_id = (
    select id
    from partner_company
    where  partner_company.company_id = job_role.company_id
);
alter table job_role_to_interview_structure
add column if not exists approval_rule_id text;

alter table job_role_to_interview_structure
    add column if not exists rejection_rule_id text;

alter table job_role_to_interview_structure rename column requires_approval TO is_manual_action_for_remaining_cases;

alter table job_role_to_interview_structure
    add column if not exists round_name text;


-- updating constrains to handle versioning of job role
ALTER TABLE skill_weightage
DROP CONSTRAINT unique_skill_per_job_role;

ALTER TABLE skill_weightage
    ADD CONSTRAINT unique_skill_per_job_role
        UNIQUE (job_role_id,job_role_version,skill_id);

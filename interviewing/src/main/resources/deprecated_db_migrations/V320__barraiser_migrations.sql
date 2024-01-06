-- Changing DB schema for incorporating versions for skillConf and jobRole

alter table job_role DROP CONSTRAINT job_role_pkey;
alter table job_role ADD PRIMARY KEY (id,version_id);

alter table job_role alter column version_id set default 0;


alter table job_role_to_interview_structure
    add column if not exists job_role_version integer NOT NULL DEFAULT 0;

alter table evaluation
    add column if not exists job_role_version integer NOT NULL DEFAULT 0;

alter table skill_weightage
    add column if not exists job_role_version integer NOT NULL DEFAULT 0;

alter table evaluation_search
    add column if not exists job_role_version integer NOT NULL DEFAULT 0;

-- Updating version of skillConf to version_id

alter table skill_interviewing_configuration
rename version to version_id;

-- Deleting excerpt from codebase

alter table default_question
drop column if exists excerpt_id;

drop table if exists excerpt;

drop table if exists interview_structure_excerpt;

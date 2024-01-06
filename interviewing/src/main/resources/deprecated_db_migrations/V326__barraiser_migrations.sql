alter table skill_interviewing_configuration
add column if not exists mandatory_expectations text;

alter table skill_interviewing_configuration
    add column if not exists barraising_expectations text;

alter table skill_interviewing_configuration
   drop column if exists evaluation_guidelines;

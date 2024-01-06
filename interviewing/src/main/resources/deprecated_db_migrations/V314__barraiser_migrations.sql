alter table job_role
drop column if  exists version;

alter table job_role
add column if not exists version_id int;

update job_role
set version_id = 0;

alter table job_role
    add column if not exists version int;


update job_role
set version = 0

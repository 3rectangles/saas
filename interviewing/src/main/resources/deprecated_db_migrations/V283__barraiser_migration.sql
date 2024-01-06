update job_role
set default_poc_email = (select distinct on (job_role_id) poc_email  from
	(select job_role_id, count(poc_email) as count_poc_mail, poc_email
  	 from evaluation group by job_role_id, poc_email
	 order by job_role_id, count_poc_mail desc) as a
	 where job_role_id=job_role.id
)

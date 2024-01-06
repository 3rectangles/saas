truncate job_role_to_interview_structure;

alter table job_role_to_interview_structure add unique(job_role_id,interview_round);

insert into job_role_to_interview_structure(id,job_role_id,interview_round,interview_structure_id,interview_structure_link,created_on,updated_on)
select uuid_generate_v4(),evaluation.job_role_id,interview.interview_round,interview.interview_structure_name,interview.final_submission_link,now(),now()
from interview inner join evaluation on interview.evaluation_id = evaluation.id
where interview.evaluation_id is not null
and evaluation.job_role_id is not null
and interview.interview_round is not null
and interview.final_submission_link is not null
order by  evaluation.job_role_id,interview.interview_round  on conflict(job_role_id,interview_round) do nothing;
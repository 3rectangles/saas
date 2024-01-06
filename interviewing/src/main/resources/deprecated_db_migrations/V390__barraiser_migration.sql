alter table interview_confirmation
add column if not exists candidate_confirmation_given_by text;

ALTER TABLE  evaluation
ADD COLUMN IF NOT EXISTS is_evaluation_score_under_review bool DEFAULT false;


create table evaluation_score_history (
    id text primary key,
    evaluation_id text,
    scoring_algo_version text,
    process_type text,
    scores jsonb,
    created_on timestamp,
    updated_on timestamp
);

update evaluation set is_evaluation_score_under_review = true  where id in
(select evaluation_id
from interview  where evaluation_id is not null
group by evaluation_id having bool_and(case
	when status = 'pending_correction' or status = 'pending_qc' Then true
	else false
end) is true) and created_on > '2022-02-10';


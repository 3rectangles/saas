ALTER TABLE evaluation_score
ADD CONSTRAINT unique_skill_per_eval_per_algo
UNIQUE (evaluation_id,skill_id,scoring_algo_version);

ALTER TABLE skill_weightage
ADD CONSTRAINT unique_skill_per_job_role
UNIQUE (job_role_id,skill_id);

ALTER TABLE interview_structure_skills
ADD CONSTRAINT unique_skill_per_interview_str
UNIQUE (interview_structure_id,skill_id);

ALTER TABLE job_role_to_interview_structure
ADD CONSTRAINT unique_interview_str_per_job_role_per_round
 UNIQUE (job_role_id,interview_round,interview_structure_id),
ALTER COLUMN interview_structure_link SET NOT NULL;




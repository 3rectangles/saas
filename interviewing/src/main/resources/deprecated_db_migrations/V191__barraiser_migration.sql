ALTER TABLE evaluation_score
ADD CONSTRAINT one_skill_per_algo_per_eval
UNIQUE (evaluation_id,skill_id,scoring_algo_version);

ALTER TABLE evaluation_recommendation
ADD CONSTRAINT one_recommendation_per_evaluation_per_recommendation_version UNIQUE (evaluation_id, recommendation_algo_version);

CREATE TABLE IF NOT EXISTS evaluation_recommendation
(
    id TEXT PRIMARY KEY,
    evaluation_id TEXT NOT NULL,
    recommendation_algo_version TEXT NOT NULL,
    recommendation_type TEXT NOT NULL,
    screening_cut_off INT NOT NULL,
    created_on TIMESTAMP,
    updated_on TIMESTAMP
);

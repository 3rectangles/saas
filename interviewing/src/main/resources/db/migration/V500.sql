CREATE TABLE IF NOT EXISTS question_category_prediction (
    id text PRIMARY KEY,
    interview_id text,
    question_id text,
    predicted_category_id text,
    request jsonb,
    ds_model_version text,
    created_on timestamp,
    updated_on timestamp
);

CREATE TABLE IF NOT EXISTS followup_question_detection (
    id text PRIMARY KEY,
    interview_id text,
    question_id text,
    master_question_id text,
    is_followup boolean,
    request jsonb,
    ds_model_version text,
    created_on timestamp,
    updated_on timestamp
);

create table if not exists expert_interview_summary(
    id text primary key,
    expert_id text,
    summary jsonb,
    created_on timestamp,
	updated_on timestamp
);

CREATE INDEX expert_id_index on expert_interview_summary(expert_id);

create table if not exists expert_interviews_prediction_history(
    id text primary key,
    expert_id text,
    payload jsonb,
    min_cost numeric,
    predicted_number_of_interviews int,
    created_on timestamp,
	updated_on timestamp
);

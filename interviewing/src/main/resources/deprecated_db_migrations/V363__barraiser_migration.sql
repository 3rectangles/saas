create table if not exists bgs_computation_variables_snapshot(
    id text primary key,
    entity_id text,
    entity_type text,
    payload jsonb,
    process_type text,
    scoring_algo_version text,
    created_on timestamp,
    updated_on timestamp
);

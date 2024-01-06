CREATE TABLE IF NOT EXISTS authz.role_to_feature_mapping(
    id text primary key,
    role_id text,
    features_to_be_shown text[],
    created_on timestamp,
    updated_on timestamp
);

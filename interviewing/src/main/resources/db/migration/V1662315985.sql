alter table evaluation
add column if not exists default_recommendation_version text;

update evaluation
set default_recommendation_version = '1';

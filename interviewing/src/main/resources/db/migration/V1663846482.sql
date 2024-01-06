alter table expert_normalised_rating
add column if not exists capped_normalised_rating numeric,
add column if not exists normalisation_version text;

alter table expert_normalised_rating_history
add column if not exists capped_normalised_rating numeric,
add column if not exists normalisation_version text;

alter table feedback
add column if not exists normalised_rating_mappings jsonb;

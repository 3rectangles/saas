alter table expert_normalised_rating
add column average decimal,
add column standard_deviation decimal;

alter table expert_normalised_rating_history
add column average decimal,
add column standard_deviation decimal;


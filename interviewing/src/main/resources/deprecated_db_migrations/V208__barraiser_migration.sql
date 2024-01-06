create table if not exists candidate_compensation_calculator_history(
id int generated always as identity,
domain_id text,
current_ctc_in_lac numeric,
work_experience_in_months int,
slope numeric,
constant numeric,
user_identity text,
created_on timestamp,
updated_on timestamp
);

alter table expert_compensation_calculator_history
add column user_identity text;

alter table general_enquiry
add column user_identity text;

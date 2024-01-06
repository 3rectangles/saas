create table expert_compensation_calculator_history(
id int generated always as identity,
hour_per_week numeric,
salary_in_lacs numeric,
min_compensation_in_lacs numeric,
max_compensation_in_lacs numeric,
ip_address text,
created_on timestamp,
updated_on timestamp
)

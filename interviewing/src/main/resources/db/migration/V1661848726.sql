alter table pricing.interview_cost
add column if not exists expert_min_price_per_hour numeric;

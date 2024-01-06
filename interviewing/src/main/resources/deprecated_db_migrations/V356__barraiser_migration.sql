alter table user_details
add column if not exists country_code text;

alter table expert
add column if not exists countries_for_which_expert_can_take_interview text[];

alter table job_role
add column if not exists country_code text,
add column if not exists eligible_countries_of_experts text[],
add column if not exists timezone text;

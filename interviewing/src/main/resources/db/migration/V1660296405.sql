alter table expert
add column if not exists min_price numeric;

update expert
set min_price = cost * multiplier;

create table IF NOT EXISTS pricing.interview_cost(
    id text primary key,
    interview_id text,
    reschedule_count integer,
    interview_cost jsonb,
    margin numeric,
    expert_id text,
    expert_cost_per_hour jsonb,
    created_on timestamp,
	updated_on timestamp
);

create table if not exists expert_history(
    id text primary key,
    expert_id text,
    cost decimal,
    currency text,
    is_active bool,
    ops_rep text,
    pan text,
    bank_account text,
    offer_letter text,
    expert_domains text[],
    multiplier numeric,
    cost_logic text,
    peer_domains text[],
    cancellation_logic text,
    score integer,
    earning_for_financial_year_2020_21 numeric,
    ifsc text,
    interviewer_referrer text,
    consultancy_referrer text,
    is_under_training boolean,
    reachout_channel text,
    companies_for_which_expert_can_take_interview text[],
    gap_between_interviews integer,
    duplicated_from text,
    resume_received_date timestamp,
    countries_for_which_expert_can_take_interview text[],
    total_interviews_completed bigint,
    is_demo_eligible boolean,
    tenant_id text,
    willing_to_switch_video_on boolean,
    min_price numeric,
    created_on timestamp,
    updated_on timestamp,
    created_by text
);

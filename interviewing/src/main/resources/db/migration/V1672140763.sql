alter table pricing.interview_cost
add column configured_margin numeric;


alter table scheduling_session
add column configured_margin numeric;


alter table pricing.interview_cost
rename column margin TO used_margin;


alter table scheduling_session
rename column margin TO used_margin;

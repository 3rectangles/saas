
create table if not exists currency(
	id text primary key,
    currency_code text ,
    symbol text,
    inr_conversion_rate decimal,
    disabled_on timestamp,
    created_on timestamp,
    updated_on timestamp
);


alter table interview_cost
add column if not exists currency_id text;

alter table interview
add column if not exists interviewee_timezone text;


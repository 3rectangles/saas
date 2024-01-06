alter table expert
drop column if exists earning_for_financial_yr_2020_21;

alter table expert
add column if not exists earning_for_financial_year_2020_21 numeric;




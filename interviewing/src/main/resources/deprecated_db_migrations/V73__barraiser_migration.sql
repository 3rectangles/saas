alter table interview
add column actual_end_date integer;

update  interview set actual_end_date = end_date,actual_start_date = start_date;
alter table parsed_resume
add column if not exists current_designation text,
add column if not exists alma_mater text;

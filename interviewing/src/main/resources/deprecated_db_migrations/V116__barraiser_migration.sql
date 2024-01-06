alter table excerpt
add column if not exists created_on timestamp,
add column if not exists updated_on timestamp,
add column if not exists operated_by text;
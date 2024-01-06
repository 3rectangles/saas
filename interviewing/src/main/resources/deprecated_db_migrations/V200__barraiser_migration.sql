create table if not exists parsed_resume(
document_id text primary key,
name text,
primary_email text,
primary_phone_no text,
address text,
experience text,
experience_in_months int,
skills text[],
current_employer text,
past_employers text[],
qualification text,
certification text,
achievements text,
hobbies text,
created_on timestamp,
updated_on timestamp,
raw_data jsonb
);

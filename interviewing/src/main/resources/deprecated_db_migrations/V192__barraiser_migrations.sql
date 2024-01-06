create table if not exists waiting_reason(
id text primary key,
reason text,
category text,
customer_displayable_reason text,
parent_group_id text,
is_active text,
process_type text,
created_on timestamp,
updated_on timestamp
)

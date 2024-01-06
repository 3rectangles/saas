create table if not exists reason (
    id text primary key,
    reason text,
    context text,
    type text,
    customer_displayable_reason text,
    is_active bool,
    display_reason text,
    entity_type text,
    order_index int,
    non_reschedulable_reason bool,
    created_on timestamp,
	updated_on timestamp
);

alter table interview
add column if not exists reopening_reason_id text;

alter table interview_history
add column if not exists reopening_reason_id text;

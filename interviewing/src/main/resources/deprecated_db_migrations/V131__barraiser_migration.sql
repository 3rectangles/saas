create table if not exists cancellation_reason (
    id text primary key,
    reason text,
    cancellation_type text,
    customer_displayable_reason text,
    parent_group_id text,
    is_active bool,
    display_reason text,
    created_on timestamp,
	updated_on timestamp
);



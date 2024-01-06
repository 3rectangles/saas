alter table cancellation_reason
add column if not exists order_index integer;

update cancellation_reason set order_index = id::integer;

alter table cancellation_reason
add column process_type text;

alter table evaluation
add column cancellation_reason_id text;

alter table evaluation
add column waiting_reason_id text


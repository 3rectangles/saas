create table if not exists interview_cost (
interview_id text primary key,
interviewer_id text,
interviewer_base_cost numeric,
multiplier numeric,
total_amount numeric,
cost_logic text,
cancellation_logic text,
payment_type text,
created_on timestamp,
updated_on timestamp,
interview_snapshot json
)

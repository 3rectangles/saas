CREATE TABLE IF NOT EXISTS interview_to_step_function_execution(
    id text  primary key,
    interview_id text,
    execution_arn text,
    flow_type text,
    created_on timestamp,
	updated_on timestamp
);

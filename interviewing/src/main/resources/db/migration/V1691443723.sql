CREATE TABLE if not exists ats_processed_events (
	id text NULL,
	calendar_event_id text NULL,
	calendar_event_start_time integer NULL,
	calendar_event_end_time integer NULL,
	interview_id text NULL,
	created_on timestamp NULL,
	updated_on timestamp NULL
);

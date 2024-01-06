create table if not exists interview_history (
    id text primary key,
    interview_id text,
    interviewer_id text,
    interviewee_id text,
    interview_round text,
    start_date int,
    end_date int,
    status text,
    created_on timestamp,
    updated_on timestamp,
    zoom_link text,
    youtube_link text,
    ops_rep text,
    evaluation_id text,
    tagging_agent text,
    last_question_end int,
    actual_start_date int,
    actual_end_date int,
    operated_by text,
    cancellation_time text,
    zoom_account_email text,
    zoom_end_time int,
    cancellation_reason_id text,
    feedback_submission_time int,
    expert_feedback_submission_time int,
    is_rescheduled bool,
    rescheduled_from text,
    submitted_code_link text,
    scheduling_platform text,
    interview_structure_id text,
    audio_link text,
    interview_start_time bigint,
    video_start_time bigint,
    interview_end_time bigint,
    video_end_time bigint,
    duplicate_reason text,
    version int,
    reschedule_count int,
    created_by text,
    source text,
    is_bad_quality bool
);

alter table interview
add column if not exists reschedule_count int;

alter table interview_cost
add column if not exists reschedule_count int;

alter table interview_confirmation
add column if not exists reschedule_count int;

alter table interview_to_step_function_execution
add column if not exists reschedule_count int;

alter table candidate_availability
add column if not exists deleted_on int;

alter table interview_to_eligible_experts
add column if not exists deleted_on int;

alter table question
add column if not exists reschedule_count int;

alter table feedback
add column if not exists reschedule_count int;

update interview set reschedule_count = 0;

update interview_cost set reschedule_count = 0;

update interview_confirmation set reschedule_count = 0;

update interview_to_step_function_execution set reschedule_count = 0;

update question set reschedule_count = 0;

update feedback set reschedule_count = 0 where type != 'PER_QUESTION';

alter table interview_cost
add column id text;

update interview_cost set id = uuid_generate_v4();

alter table interview_cost
DROP constraint interview_cost_pkey;

alter table interview_cost
ADD constraint interview_cost_pkey PRIMARY KEY (id);

insert into interview_history
select uuid_generate_v4(), i.id, interviewer_id, interviewee_id, interview_round, start_date, end_date,  ic.field_value,
ic.created_on, ic.created_on, zoom_link, youtube_link,
 ops_rep, evaluation_id, tagging_agent, last_question_end, actual_start_date, actual_end_date, operated_by, cancellation_time,
 zoom_account_email, zoom_end_time, cancellation_reason_id, feedback_submission_time, expert_feedback_submission_time,
 is_rescheduled, rescheduled_from, submitted_code_link, scheduling_platform, interview_structure_id, audio_link, interview_start_time,
 video_start_time, interview_end_time, video_end_time, duplicate_reason, version, reschedule_count, ic.created_by, ic.source, is_bad_quality
 from interview i inner join interview_change_history ic on
 i.id = ic.interview_id;

CREATE INDEX interview_history_interview_id_index ON interview_history
(
    interview_id
);

CREATE INDEX interview_to_eligible_experts_interview_id_index ON interview_to_eligible_experts
(
    interview_id
);

CREATE INDEX interview_history_interviewer_id_index ON interview_history
(
    interviewer_id
);

alter table interview alter column reschedule_count set default 0;

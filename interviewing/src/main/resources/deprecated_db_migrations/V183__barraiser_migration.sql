create table if not exists interviewee_feedback(
    interview_id text primary key,
    interviewer_knowledge integer,
    interviewer_clarity_of_questions integer,
    feedback_from_interviewer integer,
    structure_of_interview integer,
    quality_of_questions integer,
    average_rating numeric(3,2),
    any_other_feedback text,
    created_on timestamp,
    updated_on timestamp
);

create table question (
    id text primary key,
    question text,
    start_time int,
    end_time int,
    difficulty text
);

create table feedback (
    id text primary key,
    category_id text,
    rating decimal,
    weightage decimal,
    feedback text
);

create table overall_feedback (
    id text primary key,
    interview_id text,
    strength text,
    areas_of_improvement text
);

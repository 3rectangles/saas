create table if not exists excerpt(
    id text primary  key,
    skill_id text,
    expert_content text,
    candidate_content text,
    remarks text,
    duration int
);

create table if not exists  interview_structure_excerpts(
    id text primary key,
    excerpt_id text,
    interview_structure_id text
);



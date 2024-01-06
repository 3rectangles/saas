create table if not exists expert_skills_history (
    id text primary key,
    expert_id text,
    skill_id text,
    proficiency real,
    created_on timestamp,
    updated_on timestamp);

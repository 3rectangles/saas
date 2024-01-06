CREATE TABLE IF NOT EXISTS interview_structure_to_experts(
    id text primary key,
    interview_structure_id text NOT NULL,
    eligible_experts text[] NOT NULL,
    created_on timestamp,
    updated_on timestamp
);

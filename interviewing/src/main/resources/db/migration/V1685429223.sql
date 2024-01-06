ALTER table job_role_to_interview_structure add column if not exists category_rejection_json text;
ALTER table job_role_to_interview_structure add column if not exists interview_cutoff_score int;
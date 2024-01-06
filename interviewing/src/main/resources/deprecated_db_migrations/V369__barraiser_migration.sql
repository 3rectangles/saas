ALTER TABLE job_role_to_interview_structure ADD COLUMN acceptance_cutoff_score INTEGER;

ALTER TABLE job_role_to_interview_structure ADD COLUMN rejection_cutoff_score INTEGER;

UPDATE job_role_to_interview_structure SET acceptance_cutoff_score = cutoff_score;

UPDATE job_role_to_interview_structure SET rejection_cutoff_score = threshold_score;

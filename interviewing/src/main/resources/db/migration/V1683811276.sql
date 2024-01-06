ALTER TABLE public.job_role RENAME COLUMN active_candidates_count TO active_candidates_count_aggregate;

UPDATE filter SET name = 'activeCandidatesCountAggregate' WHERE name = 'activeCandidatesCount';

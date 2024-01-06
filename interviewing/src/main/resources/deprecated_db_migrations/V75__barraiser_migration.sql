ALTER TABLE evaluation
ADD COLUMN default_scoring_algo_version text;

update evaluation as e
set default_scoring_algo_version =
    (   select distinct(scoring_algo_version)
        from evaluation_score
        where evaluation_id=e.id order by scoring_algo_version desc
        limit 1
    )

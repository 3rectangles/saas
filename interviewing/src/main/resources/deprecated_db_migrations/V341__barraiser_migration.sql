CREATE INDEX company_id_index
ON evaluation_search(company_id);

CREATE INDEX deleted_on_index
ON evaluation_search(deleted_on);

CREATE INDEX poc_email_index
ON evaluation_search(poc_email);

CREATE INDEX job_role_index
ON evaluation_search(job_role_id,job_role_version);

CREATE INDEX domain_index
ON evaluation_search(domain_id);;

CREATE INDEX candidate_name_index
ON evaluation_search(candidate_name);

CREATE INDEX display_status_index
ON evaluation_search(display_status);

CREATE INDEX status_updated_on_index
ON evaluation_search(status_updated_on);


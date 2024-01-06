ALTER TABLE company
ADD column IF NOT EXISTS business_domain TEXT;


CREATE TABLE IF NOT EXISTS onboarding_request (
    id text primary key,
    jd_link text,
    job_role_of_interest text,
    poc_email text,
    hiring_manager_email text,
    channel text,
    message text,
    created_on timestamp,
    updated_on timestamp
);

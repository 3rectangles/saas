drop table if exists client_company;

create table if not exists partner_company(
    id text primary key,
    company_id text,
    hr_email text,
    hr_phone_number text,
    escalation_email text,
    created_on timestamp,
    updated_on timestamp
);
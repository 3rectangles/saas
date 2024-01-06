create table if not exists  client_company(
    id text primary key,
    company_id text,
    hr_email text,
    hr_phone_number text,
    escalation_email text,
    created_on timestamp,
    updated_on timestamp
 );

alter table interview_structure_skills
add column to_be_focussed bool;

alter table company
drop column hr_email,
drop column hr_phone_number,
drop column escalation_email;
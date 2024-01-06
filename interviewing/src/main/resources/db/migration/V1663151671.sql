CREATE TABLE IF NOT EXISTS ats_communication_template_config(
    id text primary key,
    partner_id text,
    ats_provider text,
    event_type text,
    template text,
    replacement_template text,
    title text,
    channel text,
    recepient text,
    variable_regex_mapping jsonb,
    created_on timestamp,
    updated_on timestamp
);



CREATE TABLE IF NOT EXISTS ats_to_br_interview_structure_mapping(
    id TEXT PRIMARY KEY,
    partner_id TEXT,
    ats_provider TEXT,
    br_interview_structure_id TEXT,
    ats_interview_structure_id TEXT,
    created_on TIMESTAMP,
    updated_on TIMESTAMP
);

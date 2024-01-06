create table if not exists skill_interviewing_configuration (
    id text,
    version int,
    partner_id text,
    skill_id text,
    domain_id text,
    category_coverage text,
    duration int,
    questioning_type text,
    evaluation_guidelines text,
    sample_questions text,
    created_on timestamp,
    updated_on timestamp,
    primary key(id,version)
);


create table if not exists entity_to_document_mapping (
    id text primary key,
    entity_id text,
    entity_version int,
    entity_type text,
    context text,
    document_id text,
    created_on timestamp,
    updated_on timestamp
);

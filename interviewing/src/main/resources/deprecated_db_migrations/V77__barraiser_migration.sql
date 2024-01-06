drop table if exists entity_audit_history;

create table if not exists entity_audit_history(
    id text primary key,
    entity_name text,
    entity_id text,
    operation text,
    raw_entity_state jsonb,
    operated_by text,
    created_on timestamp,
    updated_on timestamp
);

ALTER TABLE interview
ADD COLUMN operated_by text;

ALTER TABLE question
ADD COLUMN operated_by text;
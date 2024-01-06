create table if not exists entity_audit_history(
    id text primary key,
    entity_name text,
    operation text,
    raw_entity_state text,
    operated_by text,
    created_on timestamp,
    updated_on timestamp
);
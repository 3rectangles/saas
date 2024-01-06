create table if not exists event_logs (
    id text primary key,
    source text,
    timestamp bigint,
    version text,
    payload text,
    type text,
    created_on timestamp,
    updated_on timestamp
);

create table if not exists media (
id text primary key,
format text,
context text,
entity_id text,
entity_type text,
uri text,
type text,
created_on timestamp,
updated_on timestamp
)


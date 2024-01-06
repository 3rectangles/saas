create table if not exists event_template(
    id text primary key,
    event_type text,
    event_desc text,
    template text,
    created_on timestamp,
    updated_on timestamp
)

create table if not exists whatsapp_consent (
    id text primary key,
    phone text,
    consent boolean,
    created_on timestamp,
    updated_on timestamp
);

create table if not exists whatsapp_communication_history (
    id text primary key,
    phone text,
    template_id text,
    created_on timestamp ,
    updated_on timestamp
);
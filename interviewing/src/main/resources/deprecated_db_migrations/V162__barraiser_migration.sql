create table if not exists general_enquiry
(
    id text primary  key,
    name text,
    phone text,
    email text,
    topic text,
    message text,
    created_on timestamp,
    updated_on timestamp
);

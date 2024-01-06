create table if not exists otp(
    id serial primary key,
    phone text,
    email text,
    otp text,
    is_verified bool,
    is_valid bool,
    ttl int,
    created_on timestamp,
	updated_on timestamp
);
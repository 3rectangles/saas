create table if not exists user_role (
    id text  primary key,
    user_id text,
    role text,
    created_on timestamp,
	updated_on timestamp,
	deleted_on timestamp
);


create table if not exists role (
    id text  primary key,
    name text,
    created_on timestamp,
	updated_on timestamp
);


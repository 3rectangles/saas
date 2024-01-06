create table if not exists expert_normalised_rating(
    id text primary key,
    interviewer_id text,
    rating decimal,
    normalised_rating decimal,
    created_on timestamp,
    updated_on timestamp
);

create table if not exists expert_normalised_rating_history(
    id text primary key,
    interviewer_id text,
    rating decimal,
    normalised_rating decimal,
    created_on timestamp,
    updated_on timestamp
);



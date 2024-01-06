CREATE TABLE IF NOT EXISTS recurring_availability(
    id text primary key,
    user_id text NOT NULL,
    day_of_the_week text,
    slot_start_time int,
    slot_end_time int,
    timezone text,
    max_interviews_in_slot int,
    is_available boolean NOT NULL,
    created_on timestamp,
    updated_on timestamp
);

CREATE INDEX IF NOT EXISTS per_user_recurring_availability_index on recurring_availability(
    user_id,day_of_the_week
);


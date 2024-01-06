create index recurrence_slot_user_id_index on recurrence_slot(user_id);

create index exception_slot_user_id_index on exception_slot(user_id);

create index exception_slot_recurrence_id_index on exception_slot(recurrence_id);

create index booked_slot_user_id_index on booked_slot(user_id);

create index booked_slot_start_date_index on booked_slot(start_date);

create index booked_slot_user_id_start_date_index on booked_slot(user_id, start_date);

create index booked_slot_user_id_start_date_end_date_index on booked_slot(user_id, start_date, end_date);

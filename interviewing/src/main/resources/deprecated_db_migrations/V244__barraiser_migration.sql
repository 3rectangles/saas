alter table expert
add column gap_between_interviews integer;

alter table booked_slot
add column buffer integer;

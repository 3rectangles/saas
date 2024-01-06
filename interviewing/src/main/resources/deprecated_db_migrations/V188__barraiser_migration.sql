update booked_slot
set start_date = (start_date - 1800) , end_date = (end_date + 1800)
where start_date > 1624283893;

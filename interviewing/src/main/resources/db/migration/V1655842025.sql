update cancellation_reason
set customer_displayable_reason = 'Expert - Electricity/Device Issue', reason = 'Electricity/Device Issue'
where id = '45';

update cancellation_reason
set customer_displayable_reason = 'Candidate - Electricity/Device Issue', reason = 'Electricity/Device Issue'
where id = '55';

insert into cancellation_reason
(id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on, order_index)
values ('81', 'CIRCUMSTANTIAL',
'CANDIDATE_EXPERT', 'Candidate did not join the interview', true, 'Interview',
now(), now(), '85');

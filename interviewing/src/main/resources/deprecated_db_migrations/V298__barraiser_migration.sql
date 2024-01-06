update cancellation_reason
set is_active = false;

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('40', 'Not fit for the role', 'CLIENT', 'PARTNER - Not fit for the role', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('41', 'Not serious about the opportunity', 'CLIENT', 'PARTNER - Not serious about the opportunity', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('42', 'Candidate uploaded by mistake', 'CLIENT', 'PARTNER - Candidate uploaded by mistake', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('43', 'Others', 'CLIENT', 'PARTNER - Others', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('44', 'Not sure about the reason', 'EXPERT', 'EXPERT - Personal Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('45', 'Electricity Issue', 'EXPERT', 'EXPERT - Electricity Issue', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('46', 'Health Emergency', 'EXPERT', 'EXPERT - Health Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('47', 'Missed the interview communication', 'EXPERT', 'EXPERT - Personal Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('48', 'Obligation with current organization', 'EXPERT', 'EXPERT - Personal Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('49', 'Others', 'EXPERT', 'EXPERT - Personal Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('50', 'Personal Work (Travelling, Family commitment, etc.)', 'EXPERT', 'EXPERT - Personal Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('51', 'Did not receive the interview communication', 'EXPERT', 'EXPERT - Personal Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('52', 'Health Emergency', 'CANDIDATE', 'CANDIDATE - Health Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('53', 'Personal Reason (Travelling, family commitment, etc.)', 'CANDIDATE', 'CANDIDATE - Personal Reason', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('54', 'Internet Issue', 'CANDIDATE', 'CANDIDATE - Internet Issue', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('55', 'Electricity Issue', 'CANDIDATE', 'CANDIDATE - Electricity issues', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('56', 'Not prepared for the interview', 'CANDIDATE', 'CANDIDATE - Not prepared for the interview', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('57', 'Obligations with current organization', 'CANDIDATE', 'CANDIDATE - Obligations with current organization', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('58', 'Not interested in the opportunity', 'CANDIDATE', 'CANDIDATE - Not interested in the opportunity', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('59', 'Got some other offer', 'CANDIDATE', 'CANDIDATE - Got some other offer', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('60', 'Missed the interview communication', 'CANDIDATE', 'CANDIDATE - Missed the interview communication', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('61', 'Did not receive the interview communication', 'CANDIDATE', 'CANDIDATE - Did not receive the interview communication', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('62', 'Candidate left during the interview', 'CANDIDATE', 'CANDIDATE - Candidate left during the interview', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('63', 'Did not join the interview', 'CANDIDATE', 'CANDIDATE - Did not join the interview', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('64', 'Others', 'CANDIDATE', 'CANDIDATE - Others', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('65', 'Previous round score is low', 'BARRAISER', 'BARRAISER - Previous round score is low', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('66', 'Interview round created by mistake', 'BARRAISER', 'BARRAISER - Others', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('67', 'High interview cancellations', 'BARRAISER', 'BARRAISER - High interview cancellations', true, 'Evaluation', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('68', 'Not able to communicate with the candidate', 'BARRAISER', 'BARRAISER - Not able to communicate with the candidate', true, 'Evaluation', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('69', 'Technical Issue', 'BARRAISER', 'BARRAISER - Others', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('70', 'Others', 'BARRAISER', 'BARRAISER - Others', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('71', 'Not fit for the role', 'CLIENT', 'PARTNER - Not fit for the role', true, 'Evaluation', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('72', 'Not serious about the opportunity', 'CLIENT', 'PARTNER - Not serious about the opportunity', true, 'Evaluation', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('73', 'Candidate uploaded by mistake', 'CLIENT', 'PARTNER - Candidate uploaded by mistake', true, 'Evaluation', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('74', 'Internet Issue', 'EXPERT', 'EXPERT - Internet Issue', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('75', 'Could not find expert for overbooked interview', 'BARRAISER', 'EXPERT - Personal Emergency', true, 'Interview', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('76', 'Not interested in the opportunity', 'CANDIDATE', 'CANDIDATE - Not interested in the opportunity', true, 'Evaluation', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('77', 'Got some other offer', 'CANDIDATE', 'CANDIDATE - Got some other offer', true, 'Evaluation', now(), now());

insert into cancellation_reason (id, reason, type, customer_displayable_reason, is_active, process_type,  created_on, updated_on)
values ('78', 'Replacing expert for overbooked interview', 'BARRAISER', 'EXPERT - Personal Emergency', true, 'Evaluation', now(), now());


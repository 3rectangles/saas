alter table reason
add column if not exists description text;

alter table evaluation
add column if not exists candidate_rejection_reason text;

insert into reason(id, reason, context, customer_displayable_reason, is_active, display_reason, created_on, updated_on, description)
values (uuid_generate_v4(), 'Unsatisfactory performance in the interviews', 'CANDIDATE_REJECTION', 'Unsatisfactory performance in the interviews',
true, 'Unsatisfactory performance in the interviews', now(), now(), 'Low Score, Dissatisfactory Feedback from Interviewer, Culture fit issue');

insert into reason(id, reason, context, customer_displayable_reason, is_active, display_reason, created_on, updated_on, description)
values (uuid_generate_v4(), 'Unprofessional candidate', 'CANDIDATE_REJECTION', 'Unprofessional candidate',
true, 'Unprofessional candidate', now(), now(), 'Showed No Response, Not Scheduling Interviews, Cancelled multiple times, Suspected of Malpractice like cheating');

insert into reason(id, reason, context, customer_displayable_reason, is_active, display_reason, created_on, updated_on)
values (uuid_generate_v4(), 'Not Interested in the Opportunity', 'CANDIDATE_REJECTION', 'Not Interested in the Opportunity',
true, 'Not Interested in the Opportunity', now(), now());

insert into reason(id, reason, context, customer_displayable_reason, is_active, display_reason, created_on, updated_on)
values (uuid_generate_v4(), 'Position Filled or Haulted currently', 'CANDIDATE_REJECTION', 'Position Filled or Haulted currently',
true, 'UPosition Filled or Haulted currently', now(), now());

insert into reason(id, reason, context, customer_displayable_reason, is_active, display_reason, created_on, updated_on)
values (uuid_generate_v4(), 'Not satisfied with the offer', 'CANDIDATE_REJECTION', 'Not satisfied with the offer',
true, 'Not satisfied with the offer', now(), now());

insert into reason(id, reason, context, customer_displayable_reason, is_active, display_reason, created_on, updated_on, description)
values (uuid_generate_v4(), 'Others', 'CANDIDATE_REJECTION', 'Others',
true, 'Others', now(), now(), 'Candidate Notice Period too high, Health Issue, any other issue');

alter table bgs_enquiry
rename column interview_id to user_id;

update bgs_enquiry as b set user_id=(select e.user_id from evaluation as e where e.id=b.user_id);

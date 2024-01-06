create table if not exists partner_interview_summary(
    partner_id text primary key,
    average_rating numeric(3,2),
    total_review_count int,
    created_on timestamp,
    updated_on timestamp,
);
ALTER TABLE interviewee_feedback
ADD COLUMN if not exists partner_id text;

--Migration script
do
$$
declare
    f record;
begin
    for f in select if2.interview_id
   		from interviewee_feedback if2
    loop
	update interviewee_feedback
		set partner_id =
		(select pc.id  from interviewee_feedback if2
		inner join interview i on i.id  = if2 .interview_id
		inner join evaluation e  on e.id = i.evaluation_id
		inner join partner_company pc on pc.company_id  = e.company_id
		where if2.interview_id  = f.interview_id)
	where interview_id  = f.interview_id;
    end loop;
end;
$$

insert into partner_interviewing_summary(partner_id, average_rating, total_review_count, created_on, updated_on)
(select partner_id, round(avg(average_rating), 2), count(*), current_timestamp , current_timestamp  from interviewee_feedback if2 where partner_id is not null group by partner_id) ;



ALTER TABLE interview
ADD COLUMN if not exists is_pending_scheduling boolean;

ALTER TABLE interview
ADD COLUMN if not exists partner_id text;

CREATE TABLE IF NOT EXISTS waiting_information (
    evaluationId text PRIMARY KEY,
    reason text,
    waiting_reason_id text,
	updated_by varchar(100),
	created_on timestamp,
	updated_on timestamp
);

do
$$
declare
    f record;
begin
    for f in select *
   		from interview i2
    loop
	update interview
	set partner_id  = (select pc.id  from interview i
						inner join evaluation e on e.id = i.evaluation_id
						inner join partner_company pc on pc.company_id  = e.company_id
						where i.id = f.id)
	where id  = f.id;
    end loop;
end;
$$;

ALTER TABLE interview
ADD COLUMN if not exists poc_email text;

update interview i
set poc_email = e.poc_email
from evaluation e
where i.evaluation_id = e.id;

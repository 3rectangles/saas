ALTER TABLE calendar_entity
ADD COLUMN IF NOT EXISTS entity_reschedule_count integer;

UPDATE calendar_entity ce  SET entity_reschedule_count = (select reschedule_count from interview i where ce.entity_id =  i.id) where ce.status = 'CREATED'


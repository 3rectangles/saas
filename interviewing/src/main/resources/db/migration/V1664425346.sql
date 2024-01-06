alter table calendar_entity
add CONSTRAINT one_invite_per_recepient_per_interview UNIQUE (entity_id,entity_reschedule_count,status,recipient_id);

ALTER TABLE ats_to_br_evaluation
ADD UNIQUE (ats_evaluation_id);

ALTER TABLE ats_to_br_evaluation
ADD UNIQUE (br_evaluation_id);

ALTER TABLE ats_to_br_evaluation
ADD UNIQUE (ats_evaluation_id,br_evaluation_id);

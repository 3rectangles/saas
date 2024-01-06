--event_to_entity

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'SendSchedulingLinkEvent', 'EVALUATION', '{evaluationId}');

--communication_template_config

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'SendSchedulingLinkEvent', 'EMAIL', 'CANDIDATE', 'f35e99bb-7042-40f8-a82b-cb005e2c5c5b', true, now(), now());

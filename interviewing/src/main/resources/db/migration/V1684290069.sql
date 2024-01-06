ALTER TABLE filter ADD COLUMN if not exists sequence_number int;

INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('006d2ac2-2060-4f77-86ce-e1fedd184d59', 'basic_info_setup', 'JOB_ROLE', 'Intelligence not enabled',  now(), now(), '{"model":"Saas","type":"brStatus"}');

INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('70aa251d-9fe6-4d77-a940-12085243d8be', 'intelligence_enabled', 'JOB_ROLE', 'Intelligence enabled structure incomplete',  now(), now(), '{"model":"Saas","type":"brStatus"}');

INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('049f6f60-7ad0-493b-84b5-9771ccb21e6c', 'all_interviews_structured', 'JOB_ROLE', 'All interviews structured',  now(), now(), '{"model":"Saas","type":"brStatus"}');



INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('479dcc5a-902d-441c-ba16-9c117134b44b', 'active', 'JOB_ROLE', 'active',  now(), now(), '{"model":"pure_saas","type":"atsStatus"}');

INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('e4848308-a9d7-4333-82c7-b658a9315069', 'inactive', 'JOB_ROLE', 'inactive',  now(), now(), '{"model":"pure_saas","type":"atsStatus"}');

INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('2e7c2712-00d3-4307-bcd6-e58dd3b63aa2', 'draft', 'JOB_ROLE', 'draft',  now(), now(), '{"model":"pure_saas","type":"atsStatus"}');



INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('b6da1cbb-f7e9-4925-a0a4-351cc8ec7e5b', 'active', 'JOB_ROLE', 'active',  now(), now(), '{"model":"iaas","type":"brStatus"}');

INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('4fa008df-28f5-48ac-a465-180d60360a91', 'inactive', 'JOB_ROLE', 'inactive',  now(), now(), '{"model":"iaas","type":"brStatus"}');

INSERT INTO status(id, internal_status, entity_type, display_status, created_on, updated_on, context)
VALUES('1ee7832a-f552-4960-a2ae-0c931ae0b004', 'draft', 'JOB_ROLE', 'draft',  now(), now(), '{"model":"iaas","type":"brStatus"}');


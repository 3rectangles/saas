alter table ats_to_br_evaluation add column ats_job_posting_to_br_job_role_id TEXT;

insert into partner_ats_integration
(
	id,
	partner_id,
	ats_provider,
	ats_provider_displayable_name
)
values
(
	'br_sr_id',
	'test_partner_company',
	'SMART_RECRUITERS',
	'Smart Recruiters'
);

insert into ats_credential
(
	id,
	partner_ats_integration_id,
	token
)
values
(
	'br_sr_id',
	'br_sr_id',
	'DCRA1-c6a0b77537214a7db1b7503c708fb686'
);

insert into partner_ats_integration
(
	id,
	partner_id,
	ats_provider,
	ats_provider_displayable_name
)
values
(
	'slice_sr_id',
	'7fda2e41-ee61-434c-9bcf-4486eeab90f0',
	'SMART_RECRUITERS',
	'Smart Recruiters'
);

insert into ats_credential
(
	id,
	partner_ats_integration_id,
	token
)
values
(
	'slice_sr_id',
	'slice_sr_id',
	'DCRA1-c6a0b77537214a7db1b7503c708fb686'
);

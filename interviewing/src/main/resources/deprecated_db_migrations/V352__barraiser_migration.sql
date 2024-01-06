insert into email_template
(
    id,
    subject,
    body,
    query,
    header,
    branding
)
values
(
	8,
	'Urgent requirement: Candidate details needed for {{event.errorPayload.candidateName}}',
	'{{#partial "content" }}
	<p>Hello {{getJobRoles.0.company.name}} Team,</p>

	<P>We urgently require the following details to begin the evaluation process for {{event.errorPayload.candidateName}}.</p>

	<ol>
	    {{#each event.errorPayload.missingInformation}}
	        <li>{{this}}</li>
  		{{/each}}
	</ol>

	<p>Please update the information so we can prevent any delays.</p>

	<a href="https://app.barraiser.com/partner/{{partnerId}}/evaluations?st=Requires%20Action&s=createdOn,desc&" target="_blank">Update Candidate details now</a>

    <p>For any additional assistance, contact us on : support@barraiser.com</p>
	<p>Regards</p>
	<p>Team BarRaiser</p>
	{{/partial}}',
	'query GetJobRoles($input: GetJobRoleInput!) {
		getJobRoles(input: $input) {
			id
			internalDisplayName
			candidateDisplayName
			domainId
			companyId
			company {
				id
				name
			}
			domain {
				id
				name
			}
		}
	}',
	'{{getJobRoles.0.company.name}}',
	'BARRAISER'
);

insert into communication_template_config
(
    id,
    event_type,
    channel,
    recipient_type,
    template_rule,
    enabled
)
values
(
    16,
    'ATSErrorEvent',
    'EMAIL',
    'PARTNER',
    '8',
    true
);

insert into event_to_entity
(
	id,
	event_type,
	entity_type,
	entity_id_path
)
values
(
	'10',
	'ATSErrorEvent',
	'JOB_ROLE',
	'{entityId}'
);

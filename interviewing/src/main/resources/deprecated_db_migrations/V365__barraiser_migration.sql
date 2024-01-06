
DELETE FROM email_template where id = '8';

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
	'Urgent requirement: Details needed for {{event.errorPayload.candidateName}} - {{getJobRoles.0.internalDisplayName}}',
	'{{#partial "content" }}
	<p>Hello {{getJobRoles.0.company.name}} Team,</p>

	<P>We urgently require the following details to begin the evaluation process for {{event.errorPayload.candidateName}}.</p>

	<ol>
	    {{#each event.errorPayload.missingInformation}}
	        <li>{{this}}</li>
  		{{/each}}
	</ol>

	<p>Please update the information so we can prevent any delays.</p>

	<a href="{{event.errorPayload.atsCandidateProfileURL}}" target="_blank">Update Candidate details now</a>

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


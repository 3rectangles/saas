
--- partner_company
update partner_company set is_candidate_scheduling_enabled = false
where company_id = 'd464f4ba-b128-450a-8c98-ecc3c1e8bae9';


--- communication_template_config
insert into communication_template_config (id, event_type, channel, recipient_type, partner_id, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'CandidateAddition', 'EMAIL', 'CANDIDATE', 'd5d75dac-8ef5-444d-806e-2351e31a8552', 9, true, now(), now());

--- email_template
insert into email_template (id, subject, body, query, branding, created_on, updated_on)
values (9, 'Welcome to Eucloid Interview Process', '{{#partial "content" }}
<p>Hello {{getEvaluations.0.candidate.firstName}} {{getEvaluations.0.candidate.lastName}},</p>

<p>Greetings from Eucloid Data Solutions !</p>

<p>Congratulations! We are glad to inform you that your profile stood out to us, and we would like to schedule interview round with you for the position of {{getEvaluations.0.jobRole.candidateDisplayName}} at Eucloid Data Solutions, Gurgaon.</p>


{{#if getEvaluations.0.jobRole.partner.isCandidateSchedulingEnabled}}
<p>We request you to schedule your interviews from below links as soon as possible: <br/>
{{#getEvaluations.0.validInterviews}}
- Interview round {{roundNumber}} - <a href="https://www.barraiser.com/candidate-scheduling/{{id}}">https://www.barraiser.com/candidate-scheduling/{{id}}</a><br/>
{{/getEvaluations.0.validInterviews}}
</p>
{{/if}}

<p>Eucloid has collaborated with BarRaiser to bring out best in candidates. BarRaiser is an amalgamation of AI platform and expert interviewers who aim to bring global standards to interviewing across industries.</p>

<p>Feel free to reach out to us in case of any queries.</p>

<p>Contact person: Shruti (+91-8460272639)</p>

<p>Company website: www.eucloid.com</p>

<p>LinkedIn: https://www.linkedin.com/company/eucloid-data-solutions</p>

<p>Thanks,<br>
Team BarRaiser on behalf of Eucloid</p>
{{/partial}}', 'query FetchEvaluation($input: GetEvaluationInput!) {
    getEvaluations(input: $input) {
      id
      candidate {
          firstName
          lastName
          phone
      }
      jobRole {
        candidateDisplayName
        domain {
          name
        }
        company {
          name
        }
  partner {
    isCandidateSchedulingEnabled
  }
      }
      validInterviews {
        id
        roundNumber
        interviewRound
      }
    }
  }', 'BARRAISER', now(), now());

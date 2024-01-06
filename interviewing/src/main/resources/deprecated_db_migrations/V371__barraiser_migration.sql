update email_template
set body = '{{#partial "content" }}
<p>Dear {{getEvaluations.0.candidate.firstName}} {{getEvaluations.0.candidate.lastName}},</p>

<P>Congratulations! Your candidature for the role of {{getEvaluations.0.jobRole.candidateDisplayName}} ({{getEvaluations.0.jobRole.domain.name}}) has moved to next steps. </p>

{{#if getEvaluations.0.jobRole.partner.isCandidateSchedulingEnabled}}
{{#getEvaluations.0.validInterviews}}
{{#unless (stringEquals interviewRound "INTERNAL")}}
<p>We request you to schedule your interviews from below links as soon as possible: <br/>
- Interview round {{roundNumber}} - <a href="https://www.barraiser.com/candidate-scheduling/{{id}}">https://www.barraiser.com/candidate-scheduling/{{id}}</a><br/>
{{/unless}}
{{/getEvaluations.0.validInterviews}}
</p>
{{/if}}

<p>{{getEvaluations.0.jobRole.company.name}} has collaborated with BarRaiser to bring out best in candidates. BarRaiser is an amalgamation of AI platform and expert interviewers who aim to bring global standards to interviewing across industries.</p>

<p>Regards</p>
<p>Team BarRaiser on behalf of {{getEvaluations.0.jobRole.company.name}}</p>
{{/partial}}', updated_on = now() where id = 'f35e99bb-7042-40f8-a82b-cb005e2c5c5b'


update email_template
set query = 'query FetchEvaluation($input: GetEvaluationInput!) {
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
  }', updated_on = now() where id = 'f35e99bb-7042-40f8-a82b-cb005e2c5c5b'



insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'CandidateAddition', 'WHATSAPP', 'CANDIDATE',
'{{#if event.isCandidateSchedulingEnabled}}d32307f5-2448-427f-902a-f18d498a87a0{{else}}fa58a294-47b9-4531-b8b1-787577ddc9b1{{/if}}',
true, now(), now());

insert into whatsapp_template(id, query, message_bird_template_name, message_bird_template_variables, created_on, updated_on)
values ('d32307f5-2448-427f-902a-f18d498a87a0', 'query FetchEvaluation($input: GetEvaluationInput!) {
  getEvaluations(input: $input) {
    candidate {
        firstName
    }
    jobRole {
      candidateDisplayName
      company {
        name
      }
    }
    interviews {
        id
    }
  }
}', 'en_welcome_candidate_candidate_scheduling_on', '{{getEvaluations.0.candidate.firstName}}, {{getEvaluations.0.jobRole.company.name}}, {{getEvaluations.0.jobRole.candidateDisplayName}},
{{getEvaluations.0.interviews.0.id}}', now(), now());

insert into whatsapp_template(id, query, message_bird_template_name, message_bird_template_variables, created_on, updated_on)
values ('fa58a294-47b9-4531-b8b1-787577ddc9b1', 'query FetchEvaluation($input: GetEvaluationInput!) {
  getEvaluations(input: $input) {
    candidate {
        firstName
    }
    jobRole {
      candidateDisplayName
      company {
        name
      }
    }
    interviews {
        id
    }
  }
}', 'welcome_candidate_candidate_scheduling_off', '{{getEvaluations.0.candidate.firstName}}, {{getEvaluations.0.jobRole.company.name}}, {{getEvaluations.0.jobRole.candidateDisplayName}}', now(), now());


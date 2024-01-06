update slack_event_info
set template ='A new evaluation is completed:
*Name:* %s
*Job Role:* %s
*Candidate Evaluation Link:* %s

Regards,
BarRaiser Team'
where event_type = 'EvaluationCompleted';

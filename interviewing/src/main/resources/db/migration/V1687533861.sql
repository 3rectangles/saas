UPDATE slack_event_info SET
template = '{"title": ":white_check_mark: Evaluation Completed for *%s* in *%s*", "button": {"View on portal": "https://app.barraiser.com/customer/%s/evaluations/%s", "Evaluation Report": "https://app.barraiser.com/candidate-evaluation/%s"}, "footer": "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team.", "header": "%s, %s"}'
WHERE event_type = 'EvaluationCompleted' and partner_id is null;

UPDATE slack_event_info SET
template = '{"title": ":x: Evaluation Cancelled for *%s* in *%s*", "button": {"View on portal": "https://app.barraiser.com/customer/%s/evaluations/%s"}, "footer": "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team.", "header": "%s, %s"}'
WHERE event_type = 'EvaluationCancellation' and partner_id is null;

UPDATE slack_event_info SET
template = '{"title": ":mens: Candidate Added for *%s* in *%s*", "button": {"View on portal": "https://app.barraiser.com/customer/%s/evaluations/%s"}, "footer": "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team.", "header": "%s, %s"}'
WHERE event_type = 'CandidateAddition' and partner_id is null;

UPDATE slack_event_info SET
template = '{"title": ":heavy_check_mark: Interview Round %s Completed for *%s* in *%s*", "button": {"View on portal": "https://app.barraiser.com/customer/%s/evaluations/%s", "Evaluation Report": "https://app.barraiser.com/candidate-evaluation/%s"}, "footer": "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team.", "header": "%s, %s"}'
WHERE event_type = 'InterviewCompletion' and partner_id is null;

UPDATE slack_event_info SET
template = '{"title": ":warning: Interview Round %s Cancelled for *%s* in *%s*", "button": {"View on portal": "https://app.barraiser.com/customer/%s/evaluations/%s"}, "footer": "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team.", "header": "%s, %s"}'
WHERE event_type = 'InterviewCancellation' and partner_id is null;

update slack_event_info
set template = '{
    "header" : "%s, %s",
    "title" : ":mens: Candidate Added for *%s* in *%s*",
    "button" : {
        "View on portal" : "https://www.barraiser.com/partner/%s/evaluations?eid=%s"
    },
    "footer" : "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team."
}'
where event_type = 'CandidateAddition';

update slack_event_info
set template = '{
    "header" : "%s, %s",
    "title" : ":x: Evaluation Cancelled for *%s* in *%s*",
    "button" : {
        "View on portal" : "https://www.barraiser.com/partner/%s/evaluations?eid=%s"
    },
    "footer" : "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team."
}'
where event_type = 'EvaluationCancellation';

update slack_event_info
set template = '{
    "header" : "%s, %s",
    "title" : ":heavy_check_mark: Interview Round %s Completed for *%s* in *%s*",
    "button" : {
        "View on portal" : "https://www.barraiser.com/partner/%s/evaluations?eid=%s",
        "Evaluation Report" : "https://www.barraiser.com/candidate-evaluation/%s"
    },
    "footer" : "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team."
}'
where event_type = 'InterviewCompletion';

update slack_event_info
set template = '{
    "header" : "%s, %s",
    "title" : ":warning: Interview Round %s Cancelled for *%s* in *%s*",
    "button" : {
        "View on portal" : "https://www.barraiser.com/partner/%s/evaluations?eid=%s"
    },
    "footer" : "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team."
}'
where event_type = 'InterviewCancellation';

update slack_event_info
set template = '{
    "header" : "%s, %s",
    "title" : ":white_check_mark: Evaluation Completed for *%s* in *%s*",
    "button" : {
        "View on portal" : "https://www.barraiser.com/partner/%s/evaluations?eid=%s",
        "Evaluation Report" : "https://www.barraiser.com/candidate-evaluation/%s"
    },
    "footer" : "We do not monitor slack as of now, kindly visit the portal for any communication with BarRaiser team."
}'
where event_type = 'EvaluationCompleted';

ALTER TABLE slack_event_info
ALTER COLUMN template TYPE jsonb USING (template::jsonb);

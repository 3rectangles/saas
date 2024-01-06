UPDATE communication_template_config SET recipient_type = 'MENTION' WHERE event_type = 'ConversationMessage';

UPDATE email_template SET body = '<p>
                                     Hi,
                                     <br>
                                     You have been mentioned in a comment:
                                     <br>
                                     Message - {{event.message}}
                                     <br>
                                     Sent by - {{event.sender.firstName}}
                                     <br>
                                     Candidate Name - {{getEvaluations.0.candidate.firstName}}
                                     <br>
                                     Job Role - {{getEvaluations.0.jobRole.internalDisplayName}}
                                  </p>
                                  <div align="center" width="300" height="40" bgcolor="#0898A9" style="-webkit-border-radius: 5px;
                                     -moz-border-radius: 5px;
                                     border-radius: 5px;
                                     color: #ffffff;
                                     display: block;
                                     width:270px;
                                     height:40px;
                                     background-color:#0898A9;
                                     margin-left:auto;margin-right:auto">
                                     <a href="https://app.barraiser.com/candidate-evaluation/{{getEvaluations.0.id}}"
                                        style="font-size:16px;
                                        font-weight: bold;
                                        font-family: Helvetica, Arial, sans-serif;
                                        text-decoration: none;
                                        line-height:40px;
                                        width:100%;
                                        display:inline-block">
                                        <span style="color: #FFFFFF">Check Report Link</span>
                                     </a>
                                  </div>
                                  <p>
                                     Please contact support@barraiser.com in case you have any query.
                                     <br>
                                     Regards
                                     <br>
                                     Team BarRaiser
                                  </p>',
   query = 'query FetchEvaluation($input: GetEvaluationInput!) {
                getEvaluations(input: $input){
                  id
                  jobRole{
                     internalDisplayName
                  }
                  candidate{
                     firstName
                  }
                }
            }'

   where
   id = 'f35e99bb-7042-40f8-a82b-cb005e2c5bbbaacc';



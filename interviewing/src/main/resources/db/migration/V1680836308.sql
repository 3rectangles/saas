insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'ConversationMessage', 'ENTITY', '{entity,entityId}');

INSERT INTO  email_template (id,subject,body,query,"header",branding,created_on,updated_on)
	VALUES ('f35e99bb-7042-40f8-a82b-cb005e2c5bbbaacc','You have been mentioned on a candidate evaluation report
 ',
	'<p>
        Hi,
        <br>
        You have been mentioned in a comment:
        <br>
        Message - {{event.message}}
        <br>
        Sent by - {{event.sender.firstName}}
        <br>
        Candidate Name - {{getInterviews.0.interviewee.firstName}}
        <br>
        Job Role - {{getInterviews.0.jobRole.internalDisplayName}}
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
        <a href="https://app.barraiser.com/candidate-evaluation/{{getInterviews.0.evaluation.id}}"
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
     </p>','query GetInterviews($input: GetInterviewsInput!) {
           getInterviews(input: $input){
             id
             evaluation{
                id
             }
             jobRole{
                internalDisplayName
             }
             interviewee{
                firstName
             }
           }
       }','BarRaiser','BARRAISER',now(), now());



INSERT INTO communication_template_config (id,event_type,channel,recipient_type,template_rule,enabled,created_on,updated_on)
	VALUES (uuid_generate_v4(),'ConversationMessage','EMAIL','EXPERT','{{#if (in event.context.context "BgsDiscussion")}}f35e99bb-7042-40f8-a82b-cb005e2c5bbbaacc{{/if}}',true,now(),now());





insert into communication_template_config(id,event_type,channel,recipient_type,template_rule,enabled,created_on,updated_on)
values(uuid_generate_v4(),'InterviewScheduledEvent','EMAIL','CANDIDATE','e05904b9-daa9-495d-aafe-66199da2bddf',true,now(),now());

insert into email_template(id,subject,body,query,created_on,updated_on,branding)
values('e05904b9-daa9-495d-aafe-66199da2bddf',' {{getInterviews.0.jobRole.company.name}} Interview Round Scheduled - {{formatEpochInSeconds event.startDate getInterviews.0.intervieweeTimezone}}','{{#partial "content" }}
    <p>Hi {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}},</p>
    {{#if (in getInterviews.0.interviewRound "MACHINE")}}
        <p>Your interview has been scheduled on {{getInterviews.0.interviewee.timezone}} </p>
        <p><strong>What is this interview about?</strong></p>
        <p>This is an application development ( Machine Coding ) interview round ({{getInterviews.0.durationInMinutes}} mins) wherein you will be given the problem to build a small webpage using a framework on a browser based IDE. The interview will be taken by a BarRaiser Interview Expert and will be recorded.</p>
        <p>Mentioned below are the pre-interview instructions for IDE setup and coding pad: </p>
	{{#if (in getInterviews.0.interviewStructure.domain.id "d_aut" "2")}}
          <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=x2aI0TA6o64"><strong>IDE Setup Instructions</strong></a>
           </p>
    {{else}}
           <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=3zenm1owR-4"><strong>IDE Setup Instructions</strong></a>
           </p>
    {{/if}}
        <p><a style="font-style: italic;" href=“{{getInterviews.0.interviewPad.interviewerPad}}“><strong>Your Coding Pad Link</strong></a>
        </p>
        <p>Note: It is mandatory to come prepared with the setup of IDE</p>
<p>
     <div align="center" width="300" height="40" bgcolor="#0898A9" style="-webkit-border-radius: 5px;
   -moz-border-radius: 5px;
   border-radius: 5px;
   color: #ffffff;
   display: block;
   width:270px;
   height:40px;
   background-color:#0898A9;
   margin-left:auto;margin-right:auto">
   <a href="https://app.barraiser.com/interview-landing/c/{{getInterviews.0.id}}?magic_token={{getInterviews.0.interviewee.magicToken}}"
      style="font-size:16px;
      font-weight: bold;
      font-family: Helvetica, Arial, sans-serif;
      text-decoration: none;
      line-height:40px;
      width:100%;
      display:inline-block">
   <span style="color: #FFFFFF">Join Interview</span></a>
</div>
       <p>Additional Instructions for the Interview :
<ol>
  <li>Kindly login through your laptop and share your video</li>
  <li>Share your screen while you are solving the problem</li>
  <li>Ensure a stable internet and electricity connection</li>
</ol>
</p>
<p>In case of any issues, please reply to this mail. </p>
        <p>All the best,</p>
        <p>Team BarRaiser</p>
    {{else if (in getInterviews.0.interviewRound "MACHINE2")}}
        <p>Your interview has been scheduled on {{getInterviews.0.interviewee.timezone}} </p>
        <p><strong>What is this interview about?</strong></p>
        <p>This is an application development ( Machine Coding ) interview round ({{getInterviews.0.durationInMinutes}} mins) wherein you will be given the problem to build a small webpage using a framework on a browser based IDE. The interview will be taken by a BarRaiser Interview Expert and will be recorded.</p>
        <p>Mentioned below are the pre-interview instructions for IDE setup and coding pad: </p>
        {{#if (in getInterviews.0.interviewStructure.domain.id "d_aut" "2")}}
        	<p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=x2aI0TA6o64"><strong>IDE Setup Instructions</strong></a>
        	</p>
        {{else}}
        	<p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=3zenm1owR-4"><strong>IDE Setup Instructions</strong></a>
        	</p>
        {{/if}}
        <p><a style="font-style: italic;" href=“{{getInterviews.0.interviewPad.interviewerPad}}“><strong>Your Coding Pad Link</strong></a>
        </p>
        <p>Note: It is mandatory to come prepared with the setup of IDE</p>
<p>
     <div align="center" width="300" height="40" bgcolor="#0898A9" style="-webkit-border-radius: 5px;
   -moz-border-radius: 5px;
   border-radius: 5px;
   color: #ffffff;
   display: block;
   width:270px;
   height:40px;
   background-color:#0898A9;
   margin-left:auto;margin-right:auto">
   <a href="https://app.barraiser.com/interview-landing/c/{{getInterviews.0.id}}?magic_token={{getInterviews.0.interviewee.magicToken}}"
      style="font-size:16px;
      font-weight: bold;
      font-family: Helvetica, Arial, sans-serif;
      text-decoration: none;
      line-height:40px;
      width:100%;
      display:inline-block">
   <span style="color: #FFFFFF">Join Interview</span></a>
</div>
       <p>Additional Instructions for the Interview :
<ol>
  <li>Kindly login through your laptop and share your video</li>
  <li>Share your screen while you are solving the problem</li>
  <li>Ensure a stable internet and electricity connection</li>
</ol>
</p>
<p>In case of any issues, please reply to this mail. </p>
        <p>All the best,</p>
        <p>Team BarRaiser</p>
    {{else}}
        <p>We have scheduled an interview for you on .</p>
        <p>Below are the details of the interview, please go through them carefully.</p>
        <p>Interview will happen over a zoom meeting details of which can be accessed through
            <a href="https://barraiser.com/interview-landing/e/{{getInterviews.0.id}}">here</a> before the interview.
            The page also contains information on what to expect in the interview and some general guidelines of interviewing,
            it is highly recommended that you go through them and make yourself familiar.
        </p>
        <p>
            The page contains interview structure which helps you with what needs to be asked in the interview, candidate resume and
            some general guidelines of BarRaiser interviewing, it is highly recommended that you go through them much before the interview
            scheduled time and make yourself familiar and prepared for the interview.
        </p>
        <p>Key focus areas for this interview would be:</p>
        <ol style="padding-left: 20px;">
            {{#getInterviews.0.interviewStructure.categories}}
                <li> {{name}} </li>
            {{/getInterviews.0.interviewStructure.categories}}
        </ol>
        {{#if getInterviews.0.interviewStructure.specificSkills.length}}
            (specific focus on :
            {{#getInterviews.0.interviewStructure.specificSkills}}
                {{name}},
            {{/getInterviews.0.interviewStructure.specificSkills}}
            )
        {{/if}}
        <p>Wishing you all the luck !</p>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{/if}}
{{/partial}}','query GetInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input){
        id
        interviewRound
        expertScheduledStartDate
        scheduledEndDate
        durationInMinutes
jobRole {
internalDisplayName
company {
name
}
}
interviewPad {
interviewerPad
intervieweePad
}
	interviewee {
            firstName
            lastName
            timezone
            magicToken
        }
        roundLevelInterviewStructure {
            problemStatementLink
        }
        zoomLink
        interviewStructure {
		domain{
			id
		}
            categories {
                name
            }
            specificSkills {
                name
            }
        }
    }
}',now(),now(),'BARRAISER');

update email_template set subject = 'Interview Round Scheduled - {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}} {{#if (in getInterviews.0.interviewRound "MACHINE")}} ( MACHINE ) {{/if}}' ,
body = '{{#partial "content" }}
    <p>Hi {{getInterviews.0.interviewer.userDetails.firstName}} {{getInterviews.0.interviewer.userDetails.lastName}},</p>
    {{#if (in getInterviews.0.interviewRound "MACHINE")}}
        <p>We have scheduled a  {{getInterviews.0.durationInMinutes}} mins Machine Round on {{getInterviews.0.interviewer.timezone}}.</p>
	<p><strong> Key Focus areas of Interview: </strong></p>
	<p>{{getInterviews.0.interviewStructure.categories.name}}</p>
        <p><a style="font-style: italic;" href="{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}"><strong>Problem Statement </strong></a>
        </p>
         <p><a style="font-style: italic;" href="{{getInterviews.0.interviewPad.interviewerPad}}"><strong>Coding Pad </strong></a>
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
   <a href="https://app.barraiser.com/interview-landing/e/{{getInterviews.0.id}}"
      style="font-size:16px;
      font-weight: bold;
      font-family: Helvetica, Arial, sans-serif;
      text-decoration: none;
      line-height:40px;
      width:100%;
      display:inline-block">
   <span style="color: #FFFFFF">Join Interview</span></a>
</div>
	<p><strong>Note: </strong></p>
		<ol>
  			<li>We have instructed the candidate to come prepared with the IDE</li>
 			 <li>Setup Instructions to guide the candidate (in case required) to setup the coding pad can be accessed</li>
				{{#if (in getInterviews.0.interviewStructure.domain.id "d_aut" "2")}}
					<p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=3Emd-84fIWg&t=42s"><strong>IDE Setup Instructions</strong></a>
					</p>
				{{else}}
					<p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=DTHVgylbqEk"><strong>IDE Setup Instructions</strong></a>
					</p>
				{{/if}}
		 </ol>
        <p>Wishing you all the luck!</p>
        <p>BarRaiser Team</p>
    {{else if (in getInterviews.0.interviewRound "MACHINE2")}}
        <p>We have scheduled a  {{getInterviews.0.durationInMinutes}} mins Machine Round on {{getInterviews.0.interviewer.timezone}}.</p>
	<p><strong> Key Focus areas of Interview: </strong></p>
	<p>{{getInterviews.0.interviewStructure.categories.name}}</p>
        <p><a style="font-style: italic;" href="{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}"><strong>Problem Statement </strong></a>
        </p>
         <p><a style="font-style: italic;" href="{{getInterviews.0.interviewPad.interviewerPad}}"><strong>Coding Pad </strong></a>
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
   <a href="https://app.barraiser.com/interview-landing/e/{{getInterviews.0.id}}"
      style="font-size:16px;
      font-weight: bold;
      font-family: Helvetica, Arial, sans-serif;
      text-decoration: none;
      line-height:40px;
      width:100%;
      display:inline-block">
   <span style="color: #FFFFFF">Join Interview</span></a>
</div>
	<p><strong>Note: </strong></p>
		<ol>
  			<li>We have instructed the candidate to come prepared with the IDE</li>
 			 <li>Setup Instructions to guide the candidate (in case required) to setup the coding pad can be accessed</li>
 			 <li>Setup Instructions to guide the candidate (in case required) to setup the coding pad can be accessed</li>
				{{#if (in getInterviews.0.interviewStructure.domain.id "d_aut" "2")}}
                	<p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=3Emd-84fIWg&t=42s"><strong>IDE Setup Instructions</strong></a>
                	</p>
                {{else}}
                	<p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=DTHVgylbqEk"><strong>IDE Setup Instructions</strong></a>
                	</p>
                {{/if}}
		 </ol>
        <p>Wishing you all the luck!</p>
        <p>BarRaiser Team</p>
    {{else}}
        <p>We have scheduled an interview for you on .</p>
        <p>Below are the details of the interview, please go through them carefully.</p>
        <p>Interview will happen over a zoom meeting details of which can be accessed through
            <a href="https://barraiser.com/interview-landing/e/{{getInterviews.0.id}}">here</a> before the interview.
            The page also contains information on what to expect in the interview and some general guidelines of interviewing,
            it is highly recommended that you go through them and make yourself familiar.
        </p>

        <p>
            The page contains interview structure which helps you with what needs to be asked in the interview, candidate resume and
            some general guidelines of BarRaiser interviewing, it is highly recommended that you go through them much before the interview
            scheduled time and make yourself familiar and prepared for the interview.
        </p>

        <p>Key focus areas for this interview would be:</p>
        <ol style="padding-left: 20px;">
            {{#getInterviews.0.interviewStructure.categories}}
                <li> {{name}} </li>
            {{/getInterviews.0.interviewStructure.categories}}
        </ol>
        {{#if getInterviews.0.interviewStructure.specificSkills.length}}
            (specific focus on :
            {{#getInterviews.0.interviewStructure.specificSkills}}
                {{name}},
            {{/getInterviews.0.interviewStructure.specificSkills}}
            )
        {{/if}}

        <p>Wishing you all the luck !</p>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{/if}}
{{/partial}}' , query = 'query GetInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input){
        id
        interviewRound
        expertScheduledStartDate
        scheduledEndDate
	durationInMinutes
        roundLevelInterviewStructure {
            problemStatementLink
        }
	interviewer {
		userDetails {
			firstName
			lastName
			}
	timezone
	}
        interviewee {
            firstName
            lastName
            resumeUrl
	    timezone
        }
interviewPad {
interviewerPad
intervieweePad
}
        zoomLink
        interviewStructure {
	    domain{
		id
		}
            categories {
                name
            }
            specificSkills {
                name
            }
        }
    }
}' where id = 'fba6a508-0db0-485b-b962-0f6ba23f75ef'

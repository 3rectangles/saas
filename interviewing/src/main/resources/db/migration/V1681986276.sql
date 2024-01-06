UPDATE email_template SET
body = '
<p>Hi {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}},</p>
    {{#if (in getInterviews.0.interviewRound "MACHINE")}}
        <p>Your interview has been scheduled on {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.intervieweeTimezone}}</p>
        <p><strong>What is this interview about?</strong></p>
        <p>This is an application development ( Machine Coding ) interview round ({{getInterviews.0.durationInMinutes}} mins) wherein you will be given the problem to build a small webpage using a framework on a browser based IDE. The interview will be taken by a BarRaiser Interview Expert and will be recorded.</p>
        <p>Mentioned below are the pre-interview instructions for IDE setup and coding pad: </p>
    {{#if (in getInterviews.0.interviewStructure.domain.id "7" "15")}}
          <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=x2aI0TA6o64"><strong>IDE Setup Instructions</strong></a>
           </p>
    {{else}}
           <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=3zenm1owR-4"><strong>IDE Setup Instructions</strong></a>
           </p>
    {{/if}}
        <p><a style="font-style: italic;" href="{{getInterviews.0.interviewPad.intervieweePad}}"><strong>Your Coding Pad Link</strong></a>
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
        <p>Your interview has been scheduled on {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.intervieweeTimezone}} </p>
        <p><strong>What is this interview about?</strong></p>
        <p>This is an application development ( Machine Coding ) interview round ({{getInterviews.0.durationInMinutes}} mins) wherein you will be given the problem to build a small webpage using a framework on a browser based IDE. The interview will be taken by a BarRaiser Interview Expert and will be recorded.</p>
        <p>Mentioned below are the pre-interview instructions for IDE setup and coding pad: </p>
        {{#if (in getInterviews.0.interviewStructure.domain.id "7" "15")}}
            <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=x2aI0TA6o64"><strong>IDE Setup Instructions</strong></a>
            </p>
        {{else}}
            <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=3zenm1owR-4"><strong>IDE Setup Instructions</strong></a>
            </p>
        {{/if}}
        <p><a style="font-style: italic;" href="{{getInterviews.0.interviewPad.intervieweePad}}"><strong>Your Coding Pad Link</strong></a>
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
        <p>Your interview has been scheduled on {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.intervieweeTimezone}} and a calender invite has been sent as well.

    This interview will be taken by a BarRaiser expert on a zoom video call which will be recorded and hence, please read all the instructions given below: </p>
    <ol style="padding-left: 20px;">
    <li>Kindly login through a laptop and turn on your video</li>
    <li>Share your screen during coding sessions</li>
    <li>Ensure a stable internet connection </li>
    </ol>

   <p>Key focus areas for this interview would be:</p>
        <ol style="padding-left: 20px;">
            {{#getInterviews.0.interviewStructure.categories}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                    <li>{{name}}</li>
                {{/if}}
            {{/getInterviews.0.interviewStructure.categories}}
        </ol>
        {{#if getInterviews.0.interviewStructure.specificSkills.length}}
            (specific focus on :
            {{#getInterviews.0.interviewStructure.specificSkills}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        {{name}},
                {{/if}}
            {{/getInterviews.0.interviewStructure.specificSkills}}
            )
        {{/if}}
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
       </div><br/>
   Zoom passcode: 123456</p>
<p>Wishing you all the luck!</p>
        <p>BarRaiser Team</p>
    {{/if}}
',
query = 'query GetInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input){
        id
        interviewRound
        expertScheduledStartDate
    scheduledStartDate
        scheduledEndDate
    intervieweeTimezone
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
        meetingLink
        interviewStructure {
      domain{
    id
    }
            categories {
                name
                id
            }
            specificSkills {
                name
                id
            }
        }
    }
}
'
where id = 'e05904b9-daa9-495d-aafe-66199da2bddf';






UPDATE email_template SET
body = '
<p>Hi {{getInterviews.0.interviewer.userDetails.firstName}} {{getInterviews.0.interviewer.userDetails.lastName}},</p>
    {{#if (in getInterviews.0.interviewRound "MACHINE")}}
        <p>We have scheduled a  {{getInterviews.0.durationInMinutes}} mins Machine Round on {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}}.</p>

   <p><strong> Key Focus areas of Interview: </strong></p>
            {{#getInterviews.0.interviewStructure.categories}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        <li>{{name}}</li>
                {{/if}}
            {{/getInterviews.0.interviewStructure.categories}}
        {{#if getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}
        <p><a style="font-style: italic;" href="{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}"><strong>Problem Statement </strong></a>
        </p>
        {{/if}}
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
                {{#if (in getInterviews.0.interviewStructure.domain.id "7" "15")}}
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
        <p>We have scheduled a  {{getInterviews.0.durationInMinutes}} mins Machine Round on {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}}.</p>
   <p><strong> Key Focus areas of Interview: </strong></p>
            {{#getInterviews.0.interviewStructure.categories}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        <li>{{name}}</li>
                {{/if}}
            {{/getInterviews.0.interviewStructure.categories}}
        {{#if getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}
        <p><a style="font-style: italic;" href="{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}"><strong>Problem Statement </strong></a>
        </p>
        {{/if}}
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
                {{#if (in getInterviews.0.interviewStructure.domain.id "7" "15")}}
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
        <p>We have scheduled an interview for you on {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}}.</p>
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
                {{#if (stringEquals id "161")}}
                    {{else}}
                        <li>{{name}}</li>
                {{/if}}
            {{/getInterviews.0.interviewStructure.categories}}
        </ol>
        {{#if getInterviews.0.interviewStructure.specificSkills.length}}
            (specific focus on :
            {{#getInterviews.0.interviewStructure.specificSkills}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        {{name}},
                {{/if}}
            {{/getInterviews.0.interviewStructure.specificSkills}}
            )
        {{/if}}

        <p>Wishing you all the luck !</p>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{/if}}
',
query = 'query GetInterviews($input: GetInterviewsInput!) {
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
        meetingLink
        interviewStructure {
        domain{
        id
        }
            categories {
                name
                id
            }
            specificSkills {
                name
                id
            }
        }
    }
}
'
where id = 'fba6a508-0db0-485b-b962-0f6ba23f75ef';


UPDATE email_template SET
body = '
 <p>Hi {{getInterviews.0.interviewer.userDetails.firstName}} {{getInterviews.0.interviewer.userDetails.lastName}},</p>
    {{#if (in getInterviews.0.interviewRound "MACHINE")}}
        {{#if event.isOnlyCandidateChanged}}
            <p>The candidate for your next interview has been changed and request you to kindly go through the resume and interview structure.
        {{else}}
            <p>We have scheduled an interview for you which starts at {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}} and will end by
                {{formatEpochInSeconds getInterviews.0.scheduledEndDate getInterviews.0.interviewer.timezone}}</p>
        {{/if}}
        <p>Below are the details of the interview, please go through them carefully:</p>
        <p><strong>problem statement: </strong>{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}</p>
        <p><strong>Resume of the candidate: </strong>{{getInterviews.0.interviewee.resumeUrl}}</p>
        <p><strong>Zoom invite: </strong>{{getInterviews.0.meetingLink}}<br>
            Meeting Passcode: 123456
        </p>

        <p>Let us know if you have any questions.
            Thank you,</p>
        <br/>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{else if (in getInterviews.0.interviewRound "MACHINE2")}}
        {{#if event.isOnlyCandidateChanged}}
            <p>The candidate for your next interview has been changed and request you to kindly go through the resume and interview structure.
        {{else}}
            <p>We have scheduled an interview for you which starts at {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}} and will end by
                {{formatEpochInSeconds getInterviews.0.scheduledEndDate getInterviews.0.interviewer.timezone}}</p>
        {{/if}}
        <p>Below are the details of the interview, please go through them carefully:</p>
        <p>1.Kindly allow 15 minutes of problem statement reading and clarification from candidate
            end post start of the round.<br>
            <strong>problem statement: </strong>{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}
        <p><strong>2.Resume of the candidate: </strong>{{getInterviews.0.interviewee.resumeUrl}}</p>
        <p><strong>3.Zoom invite: </strong>{{getInterviews.0.meetingLink}}<br>
            Meeting Passcode: 123456
        </p>

        <p>Let us know if you have any questions.
            Thank you,</p>
        <br/>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{else}}
        {{#if event.isOnlyCandidateChanged}}
            <p>The candidate for your next interview has been changed and request you to kindly go through the resume and interview structure.
        {{else}}
            <p>We have scheduled an interview for you on {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}}.</p>
        {{/if}}
        <p>Below are the details of the interview, please go through them carefully.</p>
        <p>Interview will happen over a zoom meeting details of which can be accessed through
            <a href="https://barraiser.com/interview-landing/e/{{getInterviews.0.id}}">here</a> before the interview.
            The page also contains information on what to expect in the interview and some general guidelines of interviewing,
            it is highly recommended that you go through them and make yourself familiar.
        </p>

        <p>
            The page contains interview structure which helps you with what needs to be asked in the interview, candidate''s resume and
            some general guidelines of BarRaiser interviewing, it is highly recommended that you go through them much before the interview
            scheduled time and make yourself familiar and prepared for the interview.
        </p>

        {{#if (in getInterviews.0.interviewRound "INTERNAL")}}
            <p>Please find the BGS link for this candidate : https://app.barraiser.com/candidate-evaluation/{{getInterviews.0.evaluation.id}}</p>
        {{/if}}
        <p>Key focus areas for this interview would be:</p>
        <ol style="padding-left: 20px;">
            {{#getInterviews.0.interviewStructure.categories}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        <li>{{name}}</li>
               {{/if}}
            {{/getInterviews.0.interviewStructure.categories}}
        </ol>
        {{#if getInterviews.0.interviewStructure.specificSkills.length}}
            (specific focus on :
            {{#getInterviews.0.interviewStructure.specificSkills}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        {{name}},
                {{/if}}
            {{/getInterviews.0.interviewStructure.specificSkills}}
            )
        {{/if}}

        <p>Wishing you all the luck !</p>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{/if}}
',
query = 'query GetInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input){
        id
        interviewer {
            userDetails {
    firstName
              lastName
      }
            timezone
        }
        interviewRound
        expertScheduledStartDate
        scheduledEndDate
        roundLevelInterviewStructure {
            problemStatementLink
        }
        interviewee {
            firstName
            lastName
            resumeUrl
        }
        meetingLink
        interviewStructure {
            categories {
                name
                id
            }
            specificSkills {
                name
                id
            }
        }
  evaluation {
      id
  }
    }
}'
where id = 'e4f4cb80-1e3e-49eb-8721-869d46fc4e74';



update email_template set
body =  '<p>Hi {{getInterviews.0.interviewer.userDetails.firstName}} {{getInterviews.0.interviewer.userDetails.lastName}},</p>
                 {{#if (in getInterviews.0.interviewRound "MACHINE")}}
                     <p>We have scheduled an interview for you which starts at {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}} and will end by
                         {{formatEpochInSeconds getInterviews.0.scheduledEndDate getInterviews.0.interviewer.timezone}}</p>
                     <p>Below are the details of the interview, please go through them carefully:</p>
                     <p><strong>problem statement: </strong>{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}</p>
                     <p><strong>Resume of the candidate: </strong>{{getInterviews.0.interviewee.resumeUrl}}</p>
                     <p><strong>Zoom invite: </strong>{{getInterviews.0.meetingLink}}<br>
                         Meeting Passcode: 123456
                     </p>

                     <p>Let us know if you have any questions.
                         Thank you,</p>
                     <br/>
                     <br/>
                     <p>Regards,</p>
                     <p>BarRaiser Team</p>
                 {{else if (in getInterviews.0.interviewRound "MACHINE2")}}
                     <p>We have scheduled an interview for you which starts at {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}} and will end by
                         {{formatEpochInSeconds getInterviews.0.scheduledEndDate getInterviews.0.interviewer.timezone}}</p>
                     <p>Below are the details of the interview, please go through them carefully:</p>
                     <p>1.Kindly allow 15 minutes of problem statement reading and clarification from candidate
                         end post start of the round.<br>
                         <strong>problem statement: </strong>{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}
                     <p><strong>2.Resume of the candidate: </strong>{{getInterviews.0.interviewee.resumeUrl}}</p>
                     <p><strong>3.Zoom invite: </strong>{{getInterviews.0.meetingLink}}<br>
                         Meeting Passcode: 123456
                     </p>

                     <p>Let us know if you have any questions.
                         Thank you,</p>
                     <br/>
                     <br/>
                     <p>Regards,</p>
                     <p>BarRaiser Team</p>
                 {{else}}
                     <p>We have scheduled an interview for you on {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}}.</p>
                     <p>Below are the details of the interview, please go through them carefully.</p>
                     <p>Interview will happen over a zoom meeting details of which can be accessed through
                         <a href="https://barraiser.com/interview-landing/e/{{getInterviews.0.id}}">here</a> before the interview.
                         The page also contains information on what to expect in the interview and some general guidelines of interviewing,
                         it is highly recommended that you go through them and make yourself familiar.
                     </p>

                     <p>
                         The page contains interview structure which helps you with what needs to be asked in the interview, candidate''s resume and
                         some general guidelines of BarRaiser interviewing, it is highly recommended that you go through them much before the interview
                         scheduled time and make yourself familiar and prepared for the interview.
                     </p>

                     {{#if (in getInterviews.0.interviewRound "INTERNAL")}}
                         Please find the BGS link for this candidate : https://app.barraiser.com/candidate-evaluation/{{getInterviews.0.evaluation.id}}
                     {{/if}}
                     <p>Key focus areas for this interview would be:</p>
                     <ol style="padding-left: 20px;">
                         {{#getInterviews.0.interviewStructure.categories}}
                              {{#if (stringEquals id "161")}}
                                {{else}}
                                    <li>{{name}}</li>
                              {{/if}}
                         {{/getInterviews.0.interviewStructure.categories}}
                     </ol>
                     {{#if getInterviews.0.interviewStructure.specificSkills.length}}
                         (specific focus on :
                         {{#getInterviews.0.interviewStructure.specificSkills}}
                             {{#if (stringEquals id "161")}}
                                {{else}}
                                    {{name}},
                             {{/if}}
                         {{/getInterviews.0.interviewStructure.specificSkills}}
                         )
                     {{/if}}

                     <p>Wishing you all the luck !</p>
                     <br/>
                     <p>Regards,</p>
                     <p>BarRaiser Team</p>
                 {{/if}}',
query = 'query GetInterviews($input: GetInterviewsInput!) {
             getInterviews(input: $input){
                 id
                 interviewer {
                     userDetails {
                firstName
                        lastName
                }
                     timezone
                 }
                 interviewRound
                 expertScheduledStartDate
                 scheduledEndDate
                 roundLevelInterviewStructure {
                     problemStatementLink
                 }
                 interviewee {
                     firstName
                     lastName
                     resumeUrl
                 }
                 meetingLink
                 interviewStructure {
                     categories {
                         name
                         id
                     }
                     specificSkills {
                         name
                         id
                     }
                 }
            evaluation {
                id
            }
             }
         }
'
where
id = '85513715-8768-42ca-b22d-74ae4eacbfcc';




update email_template set
body = '
 <p>Hi {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}},</p>
    {{#if (in getInterviews.0.interviewRound "MACHINE")}}
        <p>Your interview has been scheduled on {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.intervieweeTimezone}}</p>
        <p><strong>What is this interview about?</strong></p>
        <p>This is an application development ( Machine Coding ) interview round ({{getInterviews.0.durationInMinutes}} mins) wherein you will be given the problem to build a small webpage using a framework on a browser based IDE. The interview will be taken by a BarRaiser Interview Expert and will be recorded.</p>
        <p>Mentioned below are the pre-interview instructions for IDE setup and coding pad: </p>
   {{#if (in getInterviews.0.interviewStructure.domain.id "7" "15")}}
          <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=x2aI0TA6o64"><strong>IDE Setup Instructions</strong></a>
           </p>
    {{else}}
           <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=3zenm1owR-4"><strong>IDE Setup Instructions</strong></a>
           </p>
    {{/if}}
        <p><a style="font-style: italic;" href="{{getInterviews.0.interviewPad.intervieweePad}}"><strong>Your Coding Pad Link</strong></a>
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
        <p>Your interview has been scheduled on {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.intervieweeTimezone}} </p>
        <p><strong>What is this interview about?</strong></p>
        <p>This is an application development ( Machine Coding ) interview round ({{getInterviews.0.durationInMinutes}} mins) wherein you will be given the problem to build a small webpage using a framework on a browser based IDE. The interview will be taken by a BarRaiser Interview Expert and will be recorded.</p>
        <p>Mentioned below are the pre-interview instructions for IDE setup and coding pad: </p>
        {{#if (in getInterviews.0.interviewStructure.domain.id "7" "15")}}
         <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=x2aI0TA6o64"><strong>IDE Setup Instructions</strong></a>
         </p>
        {{else}}
         <p><a style="font-style: italic;" href="https://www.youtube.com/watch?v=3zenm1owR-4"><strong>IDE Setup Instructions</strong></a>
         </p>
        {{/if}}
        <p><a style="font-style: italic;" href="{{getInterviews.0.interviewPad.intervieweePad}}"><strong>Your Coding Pad Link</strong></a>
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
        <p>Your interview has been scheduled on {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.intervieweeTimezone}} and a calender invite has been sent as well.

    This interview will be taken by a BarRaiser expert on a zoom video call which will be recorded and hence, please read all the instructions given below: </p>
    <ol style="padding-left: 20px;">
    <li>Kindly login through a laptop and turn on your video</li>
    <li>Share your screen during coding sessions</li>
    <li>Ensure a stable internet connection </li>
    </ol>
    <p>Key focus areas for this interview would be:</p>
        <ol style="padding-left: 20px;">
            {{#getInterviews.0.interviewStructure.categories}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        <li>{{name}}</li>
                {{/if}}
            {{/getInterviews.0.interviewStructure.categories}}
        </ol>
        {{#if getInterviews.0.interviewStructure.specificSkills.length}}
            (specific focus on :
            {{#getInterviews.0.interviewStructure.specificSkills}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        {{name}},
                {{/if}}
            {{/getInterviews.0.interviewStructure.specificSkills}}
            )
        {{/if}}
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
       </div><br/>
   Zoom passcode: 123456</p>
<p>Wishing you all the luck!</p>
        <p>BarRaiser Team</p>
    {{/if}}
',
query = 'query GetInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input){
        id
        interviewRound
        expertScheduledStartDate
      scheduledStartDate
        scheduledEndDate
      intervieweeTimezone
   durationInMinutes
   roundLevelInterviewStructure {
            problemStatementLink
        }
       jobRole {
            candidateDisplayName
            company {
                name
            }
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
        meetingLink
        interviewStructure {
       domain{
      id
      }
            categories {
                name
                id
            }
            specificSkills {
                name
                id
            }
        }
    }
}
'
where id = 'd847c47c-f815-493c-957b-198001b9c960';

UPDATE email_template SET
body = '
    <p>Hi {{getInterviews.0.interviewer.userDetails.firstName}} {{getInterviews.0.interviewer.userDetails.lastName}},</p>
    {{#if (in getInterviews.0.interviewRound "MACHINE")}}
        {{#if event.isOnlyCandidateChanged}}
            <p>The candidate for your next interview has been changed and request you to kindly go through the resume and interview structure.
        {{else}}
        <p>We have scheduled an interview for you which starts at {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}} and will end by
            {{formatEpochInSeconds getInterviews.0.scheduledEndDate getInterviews.0.interviewer.timezone}}</p>
        {{/if}}
        <p>Below are the details of the interview, please go through them carefully:</p>
        <p><strong>problem statement: </strong>{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}</p>
        <p><strong>Resume of the candidate: </strong>{{getInterviews.0.interviewee.resumeUrl}}</p>
        <p><strong>Zoom invite: </strong>{{getInterviews.0.meetingLink}}<br>
            Meeting Passcode: 123456
        </p>

        <p>Let us know if you have any questions.
            Thank you,</p>
        <br/>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{else if (in getInterviews.0.interviewRound "MACHINE2")}}
        {{#if event.isOnlyCandidateChanged}}
            <p>The candidate for your next interview has been changed and request you to kindly go through the resume and interview structure.
        {{else}}
            <p>We have scheduled an interview for you which starts at {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}} and will end by
                {{formatEpochInSeconds getInterviews.0.scheduledEndDate getInterviews.0.interviewer.timezone}}</p>
        {{/if}}
        <p>Below are the details of the interview, please go through them carefully:</p>
        <p>1.Kindly allow 15 minutes of problem statement reading and clarification from candidate
            end post start of the round.<br>
            <strong>problem statement: </strong>{{getInterviews.0.roundLevelInterviewStructure.problemStatementLink}}
        <p><strong>2.Resume of the candidate: </strong>{{getInterviews.0.interviewee.resumeUrl}}</p>
        <p><strong>3.Zoom invite: </strong>{{getInterviews.0.meetingLink}}<br>
            Meeting Passcode: 123456
        </p>

        <p>Let us know if you have any questions.
            Thank you,</p>
        <br/>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{else}}
        {{#if event.isOnlyCandidateChanged}}
            <p>The candidate for your next interview has been changed and request you to kindly go through the resume and interview structure.
        {{else}}
            <p>We have scheduled an interview for you on {{formatEpochInSeconds getInterviews.0.expertScheduledStartDate getInterviews.0.interviewer.timezone}}.</p>
        {{/if}}
        <p>Below are the details of the interview, please go through them carefully.</p>
        <p>Interview will happen over a zoom meeting details of which can be accessed through
            <a href="https://barraiser.com/interview-landing/e/{{getInterviews.0.id}}">here</a> before the interview.
            The page also contains information on what to expect in the interview and some general guidelines of interviewing,
            it is highly recommended that you go through them and make yourself familiar.
        </p>

        <p>
            The page contains interview structure which helps you with what needs to be asked in the interview, candidate''s resume and
            some general guidelines of BarRaiser interviewing, it is highly recommended that you go through them much before the interview
            scheduled time and make yourself familiar and prepared for the interview.
        </p>
       <p>Key focus areas for this interview would be:</p>
        <ol style="padding-left: 20px;">
            {{#getInterviews.0.interviewStructure.categories}}
                 {{#if (stringEquals id "161")}}
                    {{else}}
                        <li>{{name}}</li>
                 {{/if}}
            {{/getInterviews.0.interviewStructure.categories}}
        </ol>
        {{#if getInterviews.0.interviewStructure.specificSkills.length}}
            (specific focus on :
            {{#getInterviews.0.interviewStructure.specificSkills}}
                {{#if (stringEquals id "161")}}
                    {{else}}
                        {{name}},
                {{/if}}
            {{/getInterviews.0.interviewStructure.specificSkills}}
            )
        {{/if}}

        <p>Wishing you all the luck !</p>
        <br/>
        <p>Regards,</p>
        <p>BarRaiser Team</p>
    {{/if}}
',
query = 'query GetInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input){
        id
        interviewer {
            userDetails {
		firstName
            	lastName
	    }
            timezone
        }
        interviewRound
        expertScheduledStartDate
        scheduledEndDate
        roundLevelInterviewStructure {
            problemStatementLink
        }
        interviewee {
            firstName
            lastName
            resumeUrl
        }
        meetingLink
        interviewStructure {
            categories {
                name
                id
            }
            specificSkills {
                name
                id
            }
        }
    }
}
'
WHERE id = 'f66ac513-a527-41c2-a2d8-7fbcd6f08150';


UPDATE communication_template_config SET template_rule = '{{#if (in event.context.context "BgsDiscussion")}}f35e99bb-7042-40f8-a82b-cb005e2c5bbbaacc{{/if}}'
    WHERE event_type = 'ConversationMessage';

update email_template set body = '{{#partial "content" }}
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
        <p><a style="font-style: italic;" href=“{{getInterviews.0.interviewPad.intervieweePad}}“><strong>Your Coding Pad Link</strong></a>
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
        <p><a style="font-style: italic;" href=“{{getInterviews.0.interviewPad.intervieweePad}}“><strong>Your Coding Pad Link</strong></a>
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
        <p>Your interview has been scheduled on {{getInterviews.0.interviewee.timezone}} and a calender invite has been sent as well.

    This interview will be taken by a BarRaiser expert on a zoom video call which will be recorded and hence, please read all the instructions given below: </p>
    <ol style="padding-left: 20px;">
    <li>Kindly login through a laptop and turn on your video</li>
    <li>Share your screen during coding sessions</li>
    <li>Ensure a stable internet connection </li>
    </ol>

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
               <a href={{candidate_landing_page_url}}
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
{{/partial}}' where id = 'e05904b9-daa9-495d-aafe-66199da2bddf'

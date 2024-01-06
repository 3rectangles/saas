
insert into event_to_entity(id,event_type,entity_type,entity_id_path)
values(uuid_generate_v4(),'UserGrantedAccess','PARTNER','{partnerId}');

insert into communication_template_config(id,event_type,channel,recipient_type,template_rule,enabled,created_on,updated_on)
values(uuid_generate_v4(),'UserGrantedAccess','EMAIL','PARTNER','b087b845-3e26-491a-9fd8-b2feb8b17baa',true,now(),now());


insert into email_template(id,subject,body,header,query,branding)
values('b087b845-3e26-491a-9fd8-b2feb8b17baa','You have been invited to BarRaiser Platform','<p>Welcome from BarRaiser!</p>
<p></p>
<p>You have been invited to collaborate on the BarRaiser Platform for {{getPartner.0.companyDetails.name}}.<br>
Please use the same email id to access the platform.</p>
<p></p>
<div style="text-align:center;">
<a class="button approval" id="accept" style="width:150px;  display: inline-block; background-color:#54BE65;" href = "{{event.resource.resourceUrl}}">Access Portal</a>
</div>

<p>We would love to hear about your experience. <br>
Please reach out directly to our team for any support at support@barraiser.com.</p>
<p>Welcome aboard,<br>
The BarRaiser Team</p>'
,''
,'query GetPartner($input: PartnerInput!) {
        getPartner(input: $input) {
            companyDetails {
                name
            }
        }
  }'
,'BARRAISER');

insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query getDomains
                            {
                                getDomains{
                                   id
                                   name
                                }
                            }',
                            '{"context":"JobRolePage","model":"iaas"}','MULTISELECT','Domain','domain','{IN}','SEARCH','EMPTY','{"id":"{id}","displayName":"{name}"}','domainId',now(),now(),1);


insert into filter(id,filter_context,field_type,display_name,name,filter_type,created_on,updated_on,default_value)
values(uuid_generate_v4(),'{"context":"JobRolePage","model":"iaas"}','INT','Active Candidates','activeCandidatesCountAggregate','SORT',now(),now(),'DESCENDING');

insert into filter(id,filter_context,field_type,display_name,name,filter_type,created_on,updated_on)
values(uuid_generate_v4(),'{"context":"JobRolePage","model":"iaas"}','TEXT','Job Role Name','internalDisplayName','SORT',now(),now());

insert into filter(id,filter_context,field_type,display_name,name,filter_type,created_on,updated_on)
values(uuid_generate_v4(),'{"context":"JobRolePage","model":"pure_saas"}','TEXT','Job Role Name','internalDisplayName','SORT',now(),now());

insert into filter(id,filter_context,field_type,display_name,name,filter_type,created_on,updated_on,default_value)
values(uuid_generate_v4(),'{"context":"JobRolePage","model":"pure_saas"}','TEXT','BarRaiser Status','brStatus','SORT',now(),now(),'ASCENDING');

insert into filter(id,filter_context,field_type,display_name,name,operations_possible,filter_type,internal_name,created_on,updated_on)
values(uuid_generate_v4(),'{"context":"JobRolePage","model":"pure_saas"}','TEXT','Name','internalDisplayName','{LIKE_IGNORE_CASE}','SEARCH','internalDisplayName',now(),now());

insert into filter(id,filter_context,field_type,display_name,name,operations_possible,filter_type,internal_name,created_on,updated_on)
values(uuid_generate_v4(),'{"context":"JobRolePage","model":"iaas"}','TEXT','Name','internalDisplayName','{LIKE_IGNORE_CASE}','SEARCH','internalDisplayName',now(),now());

insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,default_value,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchJobRoleATSStatus($input: GetATSStatusInput!)
                                                                                  {
                                                                                      fetchJobRoleATSStatus(input: $input){
                                                                                         id
                                                                                         displayStatus
                                                                                      }
                                                                                  }',
                            '{"context":"JobRolePage","model":"pure_saas"}','MULTISELECT','ATS Status','atsStatus','{IN}','SEARCH','ATS_STATUS','{"id":"{id}","displayName":"{displayStatus}"}','atsStatus','479dcc5a-902d-441c-ba16-9c117134b44b',now(),now(),3);


insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchJobRoleBRStatus
                                                       {
                                                           fetchJobRoleBRStatus{
                                                              id
                                                              displayStatus
                                                           }
                                                       }',
                            '{"context":"JobRolePage","model":"pure_saas"}','MULTISELECT','BarRaiser Status','brStatus','{CONTAINS}','SEARCH','EMPTY','{"id":"{id}","displayName":"{displayStatus}"}','brStatus',now(),now(),4);


insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchHiringManagers($input: GetHiringManagersInput!)
                                                       {
                                                           fetchHiringManagers(input: $input){
                                                           userDetails{
                                                              id
                                                              email
                                                              }
                                                           }
                                                       }',
                            '{"context":"JobRolePage","model":"iaas"}','MULTISELECT','Hiring Manager','hiringManagers','{CONTAINS}','SEARCH','HIRING_MANAGER','{"id":"{userDetails,id}","displayName":"{userDetails,email}"}','hiringManagers',now(),now(),5);

insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchHiringManagers($input: GetHiringManagersInput!)
                                                       {
                                                           fetchHiringManagers(input: $input){
                                                           userDetails{
                                                              id
                                                              email
                                                              }
                                                           }
                                                       }',
                            '{"context":"JobRolePage","model":"pure_saas"}','MULTISELECT','Hiring Manager','hiringManagers','{CONTAINS}','SEARCH','HIRING_MANAGER','{"id":"{userDetails,id}","displayName":"{userDetails,email}"}','hiringManagers',now(),now(),5);

insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchRecruiters($input: GetRecruitersInput!)
                                                       {
                                                           fetchRecruiters(input: $input){
                                                               userDetails{
                                                                   id
                                                                   email
                                                               }
                                                           }
                                                       }',
                            '{"context":"JobRolePage","model":"pure_saas"}','MULTISELECT','Recruiter','recruiters','{CONTAINS}','SEARCH','RECRUITER','{"id":"{userDetails,id}","displayName":"{userDetails,email}"}','recruiters',now(),now(),6);

insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchRecruiters($input: GetRecruitersInput!)
                                                       {
                                                           fetchRecruiters(input: $input){
                                                               userDetails{
                                                                   id
                                                                   email
                                                               }
                                                           }
                                                       }',
                            '{"context":"JobRolePage","model":"iaas"}','MULTISELECT','Recruiter','recruiters','{CONTAINS}','SEARCH','RECRUITER','{"id":"{userDetails,id}","displayName":"{userDetails,email}"}','recruiters',now(),now(),6);

insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchLocations($input: GetLocationsInput!)
                                                        {
                                                            fetchLocations(input: $input){
                                                               id
                                                               name
                                                            }
                                                        }',
                            '{"context":"JobRolePage","model":"pure_saas"}','MULTISELECT','Location','locations','{CONTAINS}','SEARCH','LOCATION','{"id":"{id}","displayName":"{name}"}','locations',now(),now(),1);

insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchTeams($input: GetTeamsInput!)
                                                       {
                                                           fetchTeams(input: $input){
                                                              id
                                                              name
                                                           }
                                                       }',
                            '{"context":"JobRolePage","model":"pure_saas"}','MULTISELECT','Department','teams','{CONTAINS}','SEARCH','TEAM','{"id":"{id}","displayName":"{name}"}','teams',now(),now(),2);


insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query getJobRoleCategories
                            {
                                getJobRoleCategories{
                                   id
                                   name
                                }
                            }',
                            '{"context":"JobRolePage","model":"iaas"}','SELECT','Category','category','{IN}','SEARCH','EMPTY','{"id":"{id}","displayName":"{name}"}','category',now(),now(),4);


insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query getActiveCandidatesOptions
                            {
                                getActiveCandidatesOptions{
                                   id
                                   name
                                }
                            }',
                            '{"context":"JobRolePage","model":"iaas"}','SELECT','Active Candidates Present','activeCandidatesCountAggregate','{EQUALS}','SEARCH','EMPTY','{"id":"{id}","displayName":"{name}"}',now(),now(),7);


insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query getJobRoleStatuses
                            {
                                getJobRoleStatuses{
                                   id
                                   displayStatus
                                }
                            }',
                            '{"context":"JobRolePage","model":"iaas"}','MULTISELECT','Status','brStatus','{CONTAINS}','SEARCH','EMPTY','{"id":"{id}","displayName":"{displayStatus}"}','brStatus',now(),now(),3);

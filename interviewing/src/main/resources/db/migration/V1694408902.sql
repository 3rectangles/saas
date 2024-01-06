
----SORT Filters:

insert into filter(id,filter_context,field_type,display_name,name,filter_type,created_on,updated_on,default_value)
values(uuid_generate_v4(),'{"context":"JobRolePage","model":"saas_trial"}','TEXT','Job Role Name','internalDisplayName','SORT',now(),now(),'DESCENDING');


----Name Filter:

insert into filter(id,filter_context,field_type,display_name,name,operations_possible,filter_type,internal_name,created_on,updated_on)
values(uuid_generate_v4(),'{"context":"JobRolePage","model":"saas_trial"}','TEXT','Name','internalDisplayName','{LIKE_IGNORE_CASE}','SEARCH','internalDisplayName',now(),now());



-----SEARCH Filters:

 insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchLocations($input: GetLocationsInput!)
                                                        {
                                                            fetchLocations(input: $input){
                                                               id
                                                               name
                                                            }
                                                        }',
                            '{"context":"JobRolePage","model":"saas_trial"}','MULTISELECT','Location','locations','{CONTAINS}','SEARCH','LOCATION','{"id":"{id}","displayName":"{name}"}','locations',now(),now(),1);

insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number)
values(uuid_generate_v4(),'query fetchTeams($input: GetTeamsInput!)
                                                       {
                                                           fetchTeams(input: $input){
                                                              id
                                                              name
                                                           }
                                                       }',
                            '{"context":"JobRolePage","model":"saas_trial"}','MULTISELECT','Department','teams','{CONTAINS}','SEARCH','TEAM','{"id":"{id}","displayName":"{name}"}','teams',now(),now(),2);


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
                            '{"context":"JobRolePage","model":"saas_trial"}','MULTISELECT','Hiring Manager','hiringManagers','{CONTAINS}','SEARCH','HIRING_MANAGER','{"id":"{userDetails,id}","displayName":"{userDetails,email}"}','hiringManagers',now(),now(),4);

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
                            '{"context":"JobRolePage","model":"saas_trial"}','MULTISELECT','Recruiter','recruiters','{CONTAINS}','SEARCH','RECRUITER','{"id":"{userDetails,id}","displayName":"{userDetails,email}"}','recruiters',now(),now(),5);

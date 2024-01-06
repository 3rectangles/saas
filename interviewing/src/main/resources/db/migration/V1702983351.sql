insert into filter(id,query,filter_context,field_type,display_name,name,operations_possible,filter_type,entity_type,query_mapping,internal_name,created_on,updated_on,sequence_number, default_value)
values(uuid_generate_v4(),'query getJobRoleStatuses
                            {
                                getJobRoleStatuses{
                                   id
                                   displayStatus
                                }
                            }',
                            '{"context":"JobRolePage","model":"saas_trial"}','MULTISELECT','Status','brStatus','{CONTAINS}','SEARCH','EMPTY','{"id":"{id}","displayName":"{displayStatus}"}','brStatus',now(),now(),3, 'b6da1cbb-f7e9-4925-a0a4-351cc8ec7e5b');

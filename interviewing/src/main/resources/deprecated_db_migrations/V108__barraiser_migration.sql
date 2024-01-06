update jira_uuid
set
uuid = ( select id from evaluation where user_id = uuid order by updated_on desc limit 1)
where
(select count(*) from evaluation where user_id = uuid) > 0;

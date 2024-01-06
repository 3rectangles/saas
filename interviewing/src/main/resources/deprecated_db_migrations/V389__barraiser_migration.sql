update evaluation_search e
set job_role_name = j.internal_display_name
from job_role j
where e.job_role_id = j.id and e.job_role_version = j.version_id;

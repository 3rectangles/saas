CREATE TABLE IF NOT EXISTS highlight(
id text primary key,
interview_id text,
description text,
start_time int,
end_time int,
thumbnail_url text,
created_on timestamp,
updated_on timestamp);

CREATE TABLE IF NOT EXISTS highlight_question(
id text primary key,
highlight_id text,
description text,
offset_time int,
created_on timestamp,
updated_on timestamp);

CREATE TABLE IF NOT EXISTS user_comment(
id text primary key,
entity_id text,
entity_type text,
comment_value text,
reaction_value text,
type text,
created_by text,
offset_time int,
is_active int,
created_on timestamp,
updated_on timestamp);

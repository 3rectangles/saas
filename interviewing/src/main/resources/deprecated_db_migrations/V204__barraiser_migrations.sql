create table if not exists document (
document_id text primary key,
uploaded_by text,
file_url text,
file_name text,
s3_url text,
created_on timestamp,
updated_on timestamp
)


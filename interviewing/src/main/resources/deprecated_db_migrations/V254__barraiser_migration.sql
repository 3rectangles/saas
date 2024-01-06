alter table general_enquiry
add column if not exists url_params jsonb;

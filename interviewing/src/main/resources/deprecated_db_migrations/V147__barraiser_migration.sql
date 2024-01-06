alter table if exists interview_round_type_configuration
add column common_zoom_link text;

update interview_round_type_configuration
set common_zoom_link = 'https://us02web.zoom.us/j/82428662392';

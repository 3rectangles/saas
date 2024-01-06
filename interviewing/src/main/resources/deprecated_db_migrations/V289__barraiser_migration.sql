alter table evaluation_change_history
add column if not exists field_changed_on timestamp;

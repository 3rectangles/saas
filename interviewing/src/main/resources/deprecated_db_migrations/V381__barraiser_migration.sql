ALTER TABLE evaluation_search
ADD COLUMN if not exists have_query_for_partner boolean;

CREATE TABLE IF NOT EXISTS comment (
    id text PRIMARY KEY,
    source text,
    comment_id int,
    comment_version int,
    entity_id text,
    entity_type text,
    comment text,
	commented_by text,
	is_internal_note boolean,
	created_on timestamp,
	updated_on timestamp
);

CREATE TABLE IF NOT EXISTS comment_history (
    id text PRIMARY KEY,
    source text,
    comment_id int,
    comment_version int,
    entity_id text,
    entity_type text,
    comment text,
	commented_by text,
	is_internal_note boolean,
	created_on timestamp,
	updated_on timestamp
);

CREATE INDEX idx_comment_entity_id ON "comment"(entity_id);
CREATE INDEX idx_comment_comment_id ON "comment"(comment_id);
CREATE INDEX idx_comment_history_entity_id ON "comment_history"(entity_id);
CREATE INDEX idx_comment_history_comment_id ON "comment_history"(comment_id);

insert into reason
(id, reason, context, customer_displayable_reason, is_active, display_reason, non_reschedulable_reason, created_on, updated_on)
values
(1, 'Candidate is pending for scheduling', 'WAITING_CLIENT', 'Candidate is pending for scheduling', true, 'Candidate is pending for scheduling', false, NOW(), NOW());
insert into reason
(id, reason, context, customer_displayable_reason, is_active, display_reason, non_reschedulable_reason, created_on, updated_on)
values
(2, 'Asked to keep the candidate on holdg', 'WAITING_CLIENT', 'Asked to keep the candidate on hold', true, 'Asked to keep the candidate on hold', false, NOW(), NOW());

ALTER TABLE cancellation_reason
ADD COLUMN non_reschedulable_reason BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE cancellation_reason
SET non_reschedulable_reason = TRUE
WHERE id IN ('33', '40', '41', '42', '58','59','65');

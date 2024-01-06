update feedback
set hands_on = question.hands_on
from question
where feedback.reference_id = question.id;
CREATE INDEX qc_comment_feedback_id_index ON qc_comment
(
    feedback_id
);

CREATE INDEX interview_change_history_interview_id_index ON interview_change_history
(
    interview_id
);

CREATE INDEX evaluation_change_history_evaluation_id_index ON evaluation_change_history
(
    evaluation_id
);

CREATE INDEX question_master_question_id_index ON question
(
    master_question_id
);

CREATE INDEX evaluation_score_evaluation_id_index ON evaluation_score
(
    evaluation_id
);










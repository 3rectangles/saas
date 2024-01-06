package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "feedback_sentiment")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackSentimentDAO extends BaseModel {

    @Id
    private String id;

    @Column(name = "feedback_id")
    private String feedbackId;

    @Column(name = "feedback")
    private String feedback;

    private Double rating;

    @Column(name = "feedback_sentiment_label")
    private String feedbackSentimentLabel;

    @Column(name = "feedback_sentiment_score")
    private Double feedbackSentimentScore;

    private Boolean looksGood;
}

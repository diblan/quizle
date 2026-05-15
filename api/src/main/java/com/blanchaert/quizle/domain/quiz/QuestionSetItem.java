package com.blanchaert.quizle.domain.quiz;

import com.blanchaert.quizle.domain.question.BasicQuestion;
import com.blanchaert.quizle.domain.question.SetAnswerQuestion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "question_set_items")
@Data
public class QuestionSetItem {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_set_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private QuestionSet questionSet;

    @Column(nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionSetQuestionType questionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_question_id")
    private BasicQuestion basicQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_answer_question_id")
    private SetAnswerQuestion setAnswerQuestion;
}

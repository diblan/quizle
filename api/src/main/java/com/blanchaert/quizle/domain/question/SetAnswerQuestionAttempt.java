package com.blanchaert.quizle.domain.question;

import com.blanchaert.quizle.domain.user.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "set_answer_question_attempts")
@Data
public class SetAnswerQuestionAttempt {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private SetAnswerQuestion question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(
            name = "set_answer_question_attempt_answers",
            joinColumns = @JoinColumn(name = "attempt_id")
    )
    @OrderColumn(name = "answer_order")
    @Column(name = "answer_text", nullable = false)
    private List<String> submittedAnswers = new ArrayList<>();

    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt = Instant.now();
}

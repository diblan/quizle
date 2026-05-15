package com.blanchaert.quizle.domain.question;

import com.blanchaert.quizle.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "basic_question_attempts")
@Data
public class BasicQuestionAttempt {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private BasicQuestion question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "submitted_answer", nullable = false)
    private String submittedAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt = Instant.now();
}

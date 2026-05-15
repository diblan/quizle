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
@Table(name = "set_answer_questions")
@Data
public class SetAnswerQuestion {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "question_text", nullable = false)
    private String question;

    @Column(name = "required_answers", nullable = false)
    private int requiredAnswers;

    @ElementCollection
    @CollectionTable(
            name = "set_answer_question_solutions",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @OrderColumn(name = "solution_order")
    @Column(name = "answer_text", nullable = false)
    private List<String> answers = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}

package com.blanchaert.quizle.dto.question;

import java.time.Instant;
import java.util.UUID;

public record SetAnswerQuestionResponse(
        UUID id,
        String question,
        int requiredAnswers,
        int availableAnswers,
        String createdBy,
        Instant createdAt
) {
}

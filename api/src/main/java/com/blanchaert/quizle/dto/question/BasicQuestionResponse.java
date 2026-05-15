package com.blanchaert.quizle.dto.question;

import java.time.Instant;
import java.util.UUID;

public record BasicQuestionResponse(
        UUID id,
        String question,
        String createdBy,
        Instant createdAt
) {
}

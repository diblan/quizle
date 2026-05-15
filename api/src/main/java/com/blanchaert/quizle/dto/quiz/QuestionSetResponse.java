package com.blanchaert.quizle.dto.quiz;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record QuestionSetResponse(
        UUID id,
        String title,
        String description,
        String createdBy,
        Instant createdAt,
        List<QuestionSetQuestionResponse> questions
) {
}

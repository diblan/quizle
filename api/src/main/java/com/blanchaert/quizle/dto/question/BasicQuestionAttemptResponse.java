package com.blanchaert.quizle.dto.question;

import java.time.Instant;
import java.util.UUID;

public record BasicQuestionAttemptResponse(
        UUID attemptId,
        UUID questionId,
        String submittedAnswer,
        boolean correct,
        Instant attemptedAt
) {
}

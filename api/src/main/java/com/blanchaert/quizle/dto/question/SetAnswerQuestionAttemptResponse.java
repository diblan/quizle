package com.blanchaert.quizle.dto.question;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SetAnswerQuestionAttemptResponse(
        UUID attemptId,
        UUID questionId,
        List<String> submittedAnswers,
        int correctAnswers,
        int requiredAnswers,
        boolean correct,
        Instant attemptedAt
) {
}

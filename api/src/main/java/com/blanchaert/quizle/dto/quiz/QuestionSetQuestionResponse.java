package com.blanchaert.quizle.dto.quiz;

import com.blanchaert.quizle.domain.quiz.QuestionSetQuestionType;

import java.util.UUID;

public record QuestionSetQuestionResponse(
        int position,
        QuestionSetQuestionType type,
        UUID questionId,
        String question,
        Integer requiredAnswers,
        Integer availableAnswers
) {
}

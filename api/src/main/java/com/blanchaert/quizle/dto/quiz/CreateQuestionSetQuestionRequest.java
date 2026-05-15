package com.blanchaert.quizle.dto.quiz;

import com.blanchaert.quizle.domain.quiz.QuestionSetQuestionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionSetQuestionRequest {
    @NotNull
    private QuestionSetQuestionType type;

    @NotBlank
    private String question;

    private String answer;

    @Min(1)
    private int requiredAnswers;

    private List<@NotBlank String> answers;
}

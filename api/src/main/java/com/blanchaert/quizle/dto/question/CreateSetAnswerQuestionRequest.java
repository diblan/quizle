package com.blanchaert.quizle.dto.question;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateSetAnswerQuestionRequest {
    @NotBlank
    private String question;

    @Min(1)
    private int requiredAnswers;

    @NotEmpty
    private List<@NotBlank String> answers;
}

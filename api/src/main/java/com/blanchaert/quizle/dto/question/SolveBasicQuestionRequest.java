package com.blanchaert.quizle.dto.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolveBasicQuestionRequest {
    @NotBlank
    private String answer;
}

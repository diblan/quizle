package com.blanchaert.quizle.dto.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBasicQuestionRequest {
    @NotBlank
    private String question;

    @NotBlank
    private String answer;
}

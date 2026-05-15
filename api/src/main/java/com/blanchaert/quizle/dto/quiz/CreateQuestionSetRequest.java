package com.blanchaert.quizle.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionSetRequest {
    @NotBlank
    private String title;

    private String description;

    @NotEmpty
    private List<@Valid CreateQuestionSetQuestionRequest> questions;
}

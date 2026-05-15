package com.blanchaert.quizle.controller.quiz;

import com.blanchaert.quizle.domain.quiz.QuestionSetService;
import com.blanchaert.quizle.dto.quiz.CreateQuestionSetRequest;
import com.blanchaert.quizle.dto.quiz.QuestionSetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/question-sets")
public class QuestionSetController {
    private final QuestionSetService questionSetService;

    @PostMapping
    public ResponseEntity<QuestionSetResponse> createQuestionSet(@Valid @RequestBody CreateQuestionSetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionSetService.createQuestionSet(request));
    }

    @GetMapping
    public List<QuestionSetResponse> listQuestionSets() {
        return questionSetService.listQuestionSets();
    }

    @GetMapping("/{questionSetId}")
    public QuestionSetResponse getQuestionSet(@PathVariable UUID questionSetId) {
        return questionSetService.getQuestionSet(questionSetId);
    }
}

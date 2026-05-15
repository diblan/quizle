package com.blanchaert.quizle.controller.question;

import com.blanchaert.quizle.domain.question.BasicQuestionService;
import com.blanchaert.quizle.dto.question.BasicQuestionAttemptResponse;
import com.blanchaert.quizle.dto.question.BasicQuestionResponse;
import com.blanchaert.quizle.dto.question.CreateBasicQuestionRequest;
import com.blanchaert.quizle.dto.question.SolveBasicQuestionRequest;
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
@RequestMapping("/api/questions")
public class BasicQuestionController {
    private final BasicQuestionService questionService;

    @PostMapping
    public ResponseEntity<BasicQuestionResponse> createQuestion(@Valid @RequestBody CreateBasicQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
    }

    @GetMapping
    public List<BasicQuestionResponse> listQuestions() {
        return questionService.listQuestions();
    }

    @GetMapping("/{questionId}")
    public BasicQuestionResponse getQuestion(@PathVariable UUID questionId) {
        return questionService.getQuestion(questionId);
    }

    @PostMapping("/{questionId}/attempts")
    public BasicQuestionAttemptResponse solveQuestion(
            @PathVariable UUID questionId,
            @Valid @RequestBody SolveBasicQuestionRequest request
    ) {
        return questionService.solveQuestion(questionId, request);
    }
}

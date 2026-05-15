package com.blanchaert.quizle.controller.question;

import com.blanchaert.quizle.domain.question.SetAnswerQuestionService;
import com.blanchaert.quizle.dto.question.CreateSetAnswerQuestionRequest;
import com.blanchaert.quizle.dto.question.SetAnswerQuestionAttemptResponse;
import com.blanchaert.quizle.dto.question.SetAnswerQuestionResponse;
import com.blanchaert.quizle.dto.question.SolveSetAnswerQuestionRequest;
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
@RequestMapping("/api/set-answer-questions")
public class SetAnswerQuestionController {
    private final SetAnswerQuestionService questionService;

    @PostMapping
    public ResponseEntity<SetAnswerQuestionResponse> createQuestion(
            @Valid @RequestBody CreateSetAnswerQuestionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
    }

    @GetMapping
    public List<SetAnswerQuestionResponse> listQuestions() {
        return questionService.listQuestions();
    }

    @GetMapping("/{questionId}")
    public SetAnswerQuestionResponse getQuestion(@PathVariable UUID questionId) {
        return questionService.getQuestion(questionId);
    }

    @PostMapping("/{questionId}/attempts")
    public SetAnswerQuestionAttemptResponse solveQuestion(
            @PathVariable UUID questionId,
            @Valid @RequestBody SolveSetAnswerQuestionRequest request
    ) {
        return questionService.solveQuestion(questionId, request);
    }
}

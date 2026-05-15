package com.blanchaert.quizle.domain.question;

import com.blanchaert.quizle.domain.user.User;
import com.blanchaert.quizle.domain.user.UserRepository;
import com.blanchaert.quizle.dto.question.BasicQuestionAttemptResponse;
import com.blanchaert.quizle.dto.question.BasicQuestionResponse;
import com.blanchaert.quizle.dto.question.CreateBasicQuestionRequest;
import com.blanchaert.quizle.dto.question.SolveBasicQuestionRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicQuestionService {
    private final BasicQuestionRepository questionRepository;
    private final BasicQuestionAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    @Transactional
    public BasicQuestionResponse createQuestion(CreateBasicQuestionRequest request) {
        User user = currentUser();

        BasicQuestion question = new BasicQuestion();
        question.setQuestion(request.getQuestion().trim());
        question.setAnswer(request.getAnswer().trim());
        question.setCreatedBy(user);

        return toResponse(questionRepository.save(question));
    }

    @Transactional(readOnly = true)
    public List<BasicQuestionResponse> listQuestions() {
        return questionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BasicQuestionResponse getQuestion(UUID questionId) {
        return questionRepository.findById(questionId)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
    }

    @Transactional
    public BasicQuestionAttemptResponse solveQuestion(UUID questionId, SolveBasicQuestionRequest request) {
        User user = currentUser();
        BasicQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        String submittedAnswer = request.getAnswer().trim();
        BasicQuestionAttempt attempt = new BasicQuestionAttempt();
        attempt.setQuestion(question);
        attempt.setUser(user);
        attempt.setSubmittedAnswer(submittedAnswer);
        attempt.setCorrect(answersMatch(question.getAnswer(), submittedAnswer));

        return toResponse(attemptRepository.save(attempt));
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("A logged-in user is required");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    private boolean answersMatch(String expected, String submitted) {
        return normalize(expected).equals(normalize(submitted));
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private BasicQuestionResponse toResponse(BasicQuestion question) {
        return new BasicQuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getCreatedBy().getUsername(),
                question.getCreatedAt()
        );
    }

    private BasicQuestionAttemptResponse toResponse(BasicQuestionAttempt attempt) {
        return new BasicQuestionAttemptResponse(
                attempt.getId(),
                attempt.getQuestion().getId(),
                attempt.getSubmittedAnswer(),
                attempt.isCorrect(),
                attempt.getAttemptedAt()
        );
    }
}

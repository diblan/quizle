package com.blanchaert.quizle.domain.question;

import com.blanchaert.quizle.domain.user.User;
import com.blanchaert.quizle.domain.user.UserRepository;
import com.blanchaert.quizle.dto.question.CreateSetAnswerQuestionRequest;
import com.blanchaert.quizle.dto.question.SetAnswerQuestionAttemptResponse;
import com.blanchaert.quizle.dto.question.SetAnswerQuestionResponse;
import com.blanchaert.quizle.dto.question.SolveSetAnswerQuestionRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SetAnswerQuestionService {
    private final SetAnswerQuestionRepository questionRepository;
    private final SetAnswerQuestionAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    @Transactional
    public SetAnswerQuestionResponse createQuestion(CreateSetAnswerQuestionRequest request) {
        User user = currentUser();
        List<String> answers = trimmedDistinct(request.getAnswers());

        if (request.getRequiredAnswers() > answers.size()) {
            throw new IllegalArgumentException("Required answers cannot exceed available answers");
        }

        SetAnswerQuestion question = new SetAnswerQuestion();
        question.setQuestion(request.getQuestion().trim());
        question.setRequiredAnswers(request.getRequiredAnswers());
        question.setAnswers(answers);
        question.setCreatedBy(user);

        return toResponse(questionRepository.save(question));
    }

    @Transactional(readOnly = true)
    public List<SetAnswerQuestionResponse> listQuestions() {
        return questionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SetAnswerQuestionResponse getQuestion(UUID questionId) {
        return questionRepository.findById(questionId)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
    }

    @Transactional
    public SetAnswerQuestionAttemptResponse solveQuestion(UUID questionId, SolveSetAnswerQuestionRequest request) {
        User user = currentUser();
        SetAnswerQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        List<String> submittedAnswers = trimmedDistinct(request.getAnswers());
        if (submittedAnswers.size() != question.getRequiredAnswers()) {
            throw new IllegalArgumentException("Submit exactly " + question.getRequiredAnswers() + " answers");
        }

        int correctAnswers = countCorrectAnswers(question.getAnswers(), submittedAnswers);

        SetAnswerQuestionAttempt attempt = new SetAnswerQuestionAttempt();
        attempt.setQuestion(question);
        attempt.setUser(user);
        attempt.setSubmittedAnswers(submittedAnswers);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setCorrect(correctAnswers == question.getRequiredAnswers());

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

    private int countCorrectAnswers(List<String> expectedAnswers, List<String> submittedAnswers) {
        Set<String> expected = expectedAnswers.stream()
                .map(this::normalize)
                .collect(Collectors.toSet());

        return (int) submittedAnswers.stream()
                .map(this::normalize)
                .filter(expected::contains)
                .count();
    }

    private List<String> trimmedDistinct(List<String> values) {
        return values.stream()
                .map(String::trim)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                this::normalize,
                                Function.identity(),
                                (first, duplicate) -> first,
                                LinkedHashMap::new
                        ),
                        map -> List.copyOf(map.values())
                ));
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private SetAnswerQuestionResponse toResponse(SetAnswerQuestion question) {
        return new SetAnswerQuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getRequiredAnswers(),
                question.getAnswers().size(),
                question.getCreatedBy().getUsername(),
                question.getCreatedAt()
        );
    }

    private SetAnswerQuestionAttemptResponse toResponse(SetAnswerQuestionAttempt attempt) {
        return new SetAnswerQuestionAttemptResponse(
                attempt.getId(),
                attempt.getQuestion().getId(),
                List.copyOf(attempt.getSubmittedAnswers()),
                attempt.getCorrectAnswers(),
                attempt.getQuestion().getRequiredAnswers(),
                attempt.isCorrect(),
                attempt.getAttemptedAt()
        );
    }
}

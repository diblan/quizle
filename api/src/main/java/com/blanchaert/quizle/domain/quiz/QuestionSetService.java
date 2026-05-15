package com.blanchaert.quizle.domain.quiz;

import com.blanchaert.quizle.domain.question.BasicQuestion;
import com.blanchaert.quizle.domain.question.BasicQuestionRepository;
import com.blanchaert.quizle.domain.question.SetAnswerQuestion;
import com.blanchaert.quizle.domain.question.SetAnswerQuestionRepository;
import com.blanchaert.quizle.domain.user.User;
import com.blanchaert.quizle.domain.user.UserRepository;
import com.blanchaert.quizle.dto.quiz.CreateQuestionSetQuestionRequest;
import com.blanchaert.quizle.dto.quiz.CreateQuestionSetRequest;
import com.blanchaert.quizle.dto.quiz.QuestionSetQuestionResponse;
import com.blanchaert.quizle.dto.quiz.QuestionSetResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class QuestionSetService {
    private final QuestionSetRepository questionSetRepository;
    private final BasicQuestionRepository basicQuestionRepository;
    private final SetAnswerQuestionRepository setAnswerQuestionRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuestionSetResponse createQuestionSet(CreateQuestionSetRequest request) {
        User user = currentUser();

        QuestionSet questionSet = new QuestionSet();
        questionSet.setTitle(request.getTitle().trim());
        questionSet.setDescription(trimToNull(request.getDescription()));
        questionSet.setCreatedBy(user);

        for (int index = 0; index < request.getQuestions().size(); index++) {
            questionSet.addQuestion(createItem(user, request.getQuestions().get(index), index + 1));
        }

        return toResponse(questionSetRepository.save(questionSet));
    }

    @Transactional(readOnly = true)
    public List<QuestionSetResponse> listQuestionSets() {
        return questionSetRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionSetResponse getQuestionSet(UUID questionSetId) {
        return questionSetRepository.findById(questionSetId)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Question set not found"));
    }

    private QuestionSetItem createItem(User user, CreateQuestionSetQuestionRequest request, int position) {
        QuestionSetItem item = new QuestionSetItem();
        item.setPosition(position);
        item.setQuestionType(request.getType());

        switch (request.getType()) {
            case BASIC -> item.setBasicQuestion(createBasicQuestion(user, request));
            case SET_ANSWER -> item.setSetAnswerQuestion(createSetAnswerQuestion(user, request));
            default -> throw new IllegalArgumentException("Unsupported question type");
        }

        return item;
    }

    private BasicQuestion createBasicQuestion(User user, CreateQuestionSetQuestionRequest request) {
        if (request.getAnswer() == null || request.getAnswer().isBlank()) {
            throw new IllegalArgumentException("Basic questions require an answer");
        }

        BasicQuestion question = new BasicQuestion();
        question.setQuestion(request.getQuestion().trim());
        question.setAnswer(request.getAnswer().trim());
        question.setCreatedBy(user);
        return basicQuestionRepository.save(question);
    }

    private SetAnswerQuestion createSetAnswerQuestion(User user, CreateQuestionSetQuestionRequest request) {
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("Set-answer questions require answers");
        }

        List<String> answers = trimmedDistinct(request.getAnswers());
        if (answers.isEmpty()) {
            throw new IllegalArgumentException("Set-answer questions require answers");
        }
        if (request.getRequiredAnswers() < 1) {
            throw new IllegalArgumentException("Set-answer questions require at least one required answer");
        }
        if (request.getRequiredAnswers() > answers.size()) {
            throw new IllegalArgumentException("Required answers cannot exceed available answers");
        }

        SetAnswerQuestion question = new SetAnswerQuestion();
        question.setQuestion(request.getQuestion().trim());
        question.setRequiredAnswers(request.getRequiredAnswers());
        question.setAnswers(answers);
        question.setCreatedBy(user);
        return setAnswerQuestionRepository.save(question);
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("A logged-in user is required");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    private List<String> trimmedDistinct(List<String> values) {
        return values.stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
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

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private QuestionSetResponse toResponse(QuestionSet questionSet) {
        return new QuestionSetResponse(
                questionSet.getId(),
                questionSet.getTitle(),
                questionSet.getDescription(),
                questionSet.getCreatedBy().getUsername(),
                questionSet.getCreatedAt(),
                questionSet.getQuestions().stream()
                        .map(this::toQuestionResponse)
                        .toList()
        );
    }

    private QuestionSetQuestionResponse toQuestionResponse(QuestionSetItem item) {
        if (item.getQuestionType() == QuestionSetQuestionType.BASIC) {
            BasicQuestion question = item.getBasicQuestion();
            return new QuestionSetQuestionResponse(
                    item.getPosition(),
                    item.getQuestionType(),
                    question.getId(),
                    question.getQuestion(),
                    null,
                    null
            );
        }

        SetAnswerQuestion question = item.getSetAnswerQuestion();
        return new QuestionSetQuestionResponse(
                item.getPosition(),
                item.getQuestionType(),
                question.getId(),
                question.getQuestion(),
                question.getRequiredAnswers(),
                question.getAnswers().size()
        );
    }
}

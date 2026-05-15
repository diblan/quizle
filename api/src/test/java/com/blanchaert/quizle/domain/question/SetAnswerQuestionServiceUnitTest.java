package com.blanchaert.quizle.domain.question;

import com.blanchaert.quizle.domain.user.Role;
import com.blanchaert.quizle.domain.user.User;
import com.blanchaert.quizle.domain.user.UserRepository;
import com.blanchaert.quizle.dto.question.CreateSetAnswerQuestionRequest;
import com.blanchaert.quizle.dto.question.SolveSetAnswerQuestionRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class SetAnswerQuestionServiceUnitTest {

    private SetAnswerQuestionRepository questionRepository;
    private SetAnswerQuestionAttemptRepository attemptRepository;
    private UserRepository userRepository;
    private SetAnswerQuestionService questionService;
    private User user;

    @BeforeEach
    void setUp() {
        questionRepository = mock(SetAnswerQuestionRepository.class);
        attemptRepository = mock(SetAnswerQuestionAttemptRepository.class);
        userRepository = mock(UserRepository.class);
        questionService = new SetAnswerQuestionService(questionRepository, attemptRepository, userRepository);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("learner");
        user.setEmail("learner@example.com");
        user.setPasswordHash("hash");
        user.setRole(Role.USER);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("learner", "password", List.of())
        );
        when(userRepository.findByUsername("learner")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createQuestion_savesQuestionWithRequiredAnswerCount() {
        CreateSetAnswerQuestionRequest request = new CreateSetAnswerQuestionRequest();
        request.setQuestion(" Name the layers of the OSI model ");
        request.setRequiredAnswers(7);
        request.setAnswers(List.of(
                " Physical ",
                "Data Link",
                "Network",
                "Transport",
                "Session",
                "Presentation",
                "Application"
        ));
        when(questionRepository.save(any(SetAnswerQuestion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        questionService.createQuestion(request);

        ArgumentCaptor<SetAnswerQuestion> questionCaptor = ArgumentCaptor.forClass(SetAnswerQuestion.class);
        verify(questionRepository).save(questionCaptor.capture());
        SetAnswerQuestion savedQuestion = questionCaptor.getValue();
        assertEquals("Name the layers of the OSI model", savedQuestion.getQuestion());
        assertEquals(7, savedQuestion.getRequiredAnswers());
        assertEquals(List.of(
                "Physical",
                "Data Link",
                "Network",
                "Transport",
                "Session",
                "Presentation",
                "Application"
        ), savedQuestion.getAnswers());
        assertEquals(user, savedQuestion.getCreatedBy());
    }

    @Test
    void createQuestion_rejectsRequiredCountGreaterThanAvailableAnswers() {
        CreateSetAnswerQuestionRequest request = new CreateSetAnswerQuestionRequest();
        request.setQuestion("Name three primary colors");
        request.setRequiredAnswers(4);
        request.setAnswers(List.of("red", "blue", "yellow"));

        assertThrows(IllegalArgumentException.class, () -> questionService.createQuestion(request));
    }

    @Test
    void solveQuestion_storesAttemptAndMarksAllRequiredAnswersCorrect() {
        UUID questionId = UUID.randomUUID();
        SetAnswerQuestion question = new SetAnswerQuestion();
        question.setId(questionId);
        question.setQuestion("Name the layers of the OSI model");
        question.setRequiredAnswers(7);
        question.setAnswers(List.of(
                "Physical",
                "Data Link",
                "Network",
                "Transport",
                "Session",
                "Presentation",
                "Application"
        ));
        question.setCreatedBy(user);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(attemptRepository.save(any(SetAnswerQuestionAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SolveSetAnswerQuestionRequest request = new SolveSetAnswerQuestionRequest();
        request.setAnswers(List.of(
                "physical",
                " data link ",
                "NETWORK",
                "Transport",
                "Session",
                "Presentation",
                "Application"
        ));

        var response = questionService.solveQuestion(questionId, request);

        ArgumentCaptor<SetAnswerQuestionAttempt> attemptCaptor = ArgumentCaptor.forClass(SetAnswerQuestionAttempt.class);
        verify(attemptRepository).save(attemptCaptor.capture());
        SetAnswerQuestionAttempt savedAttempt = attemptCaptor.getValue();
        assertEquals(question, savedAttempt.getQuestion());
        assertEquals(user, savedAttempt.getUser());
        assertEquals(7, savedAttempt.getCorrectAnswers());
        assertTrue(savedAttempt.isCorrect());
        assertEquals(List.of(
                "physical",
                "data link",
                "NETWORK",
                "Transport",
                "Session",
                "Presentation",
                "Application"
        ), savedAttempt.getSubmittedAnswers());
        assertTrue(response.correct());
    }

    @Test
    void solveQuestion_requiresExactlyTheConfiguredNumberOfAnswers() {
        UUID questionId = UUID.randomUUID();
        SetAnswerQuestion question = new SetAnswerQuestion();
        question.setId(questionId);
        question.setQuestion("Name the layers of the OSI model");
        question.setRequiredAnswers(7);
        question.setAnswers(List.of(
                "Physical",
                "Data Link",
                "Network",
                "Transport",
                "Session",
                "Presentation",
                "Application"
        ));
        question.setCreatedBy(user);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        SolveSetAnswerQuestionRequest request = new SolveSetAnswerQuestionRequest();
        request.setAnswers(List.of("Physical", "Data Link"));

        assertThrows(IllegalArgumentException.class, () -> questionService.solveQuestion(questionId, request));
    }
}

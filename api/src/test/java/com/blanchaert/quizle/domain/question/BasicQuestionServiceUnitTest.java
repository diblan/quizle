package com.blanchaert.quizle.domain.question;

import com.blanchaert.quizle.domain.user.Role;
import com.blanchaert.quizle.domain.user.User;
import com.blanchaert.quizle.domain.user.UserRepository;
import com.blanchaert.quizle.dto.question.CreateBasicQuestionRequest;
import com.blanchaert.quizle.dto.question.SolveBasicQuestionRequest;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class BasicQuestionServiceUnitTest {

    private BasicQuestionRepository questionRepository;
    private BasicQuestionAttemptRepository attemptRepository;
    private UserRepository userRepository;
    private BasicQuestionService questionService;
    private User user;

    @BeforeEach
    void setUp() {
        questionRepository = mock(BasicQuestionRepository.class);
        attemptRepository = mock(BasicQuestionAttemptRepository.class);
        userRepository = mock(UserRepository.class);
        questionService = new BasicQuestionService(questionRepository, attemptRepository, userRepository);

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
    void createQuestion_savesQuestionForAuthenticatedUser() {
        CreateBasicQuestionRequest request = new CreateBasicQuestionRequest();
        request.setQuestion(" What is 2 + 2? ");
        request.setAnswer(" 4 ");
        when(questionRepository.save(org.mockito.ArgumentMatchers.any(BasicQuestion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        questionService.createQuestion(request);

        ArgumentCaptor<BasicQuestion> questionCaptor = ArgumentCaptor.forClass(BasicQuestion.class);
        verify(questionRepository).save(questionCaptor.capture());
        BasicQuestion savedQuestion = questionCaptor.getValue();
        assertEquals("What is 2 + 2?", savedQuestion.getQuestion());
        assertEquals("4", savedQuestion.getAnswer());
        assertEquals(user, savedQuestion.getCreatedBy());
    }

    @Test
    void solveQuestion_storesAttemptAndMarksCaseInsensitiveAnswerCorrect() {
        UUID questionId = UUID.randomUUID();
        BasicQuestion question = new BasicQuestion();
        question.setId(questionId);
        question.setQuestion("Capital of Belgium?");
        question.setAnswer("Brussels");
        question.setCreatedBy(user);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(attemptRepository.save(org.mockito.ArgumentMatchers.any(BasicQuestionAttempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SolveBasicQuestionRequest request = new SolveBasicQuestionRequest();
        request.setAnswer(" brussels ");

        var response = questionService.solveQuestion(questionId, request);

        ArgumentCaptor<BasicQuestionAttempt> attemptCaptor = ArgumentCaptor.forClass(BasicQuestionAttempt.class);
        verify(attemptRepository).save(attemptCaptor.capture());
        BasicQuestionAttempt savedAttempt = attemptCaptor.getValue();
        assertEquals(question, savedAttempt.getQuestion());
        assertEquals(user, savedAttempt.getUser());
        assertEquals("brussels", savedAttempt.getSubmittedAnswer());
        assertTrue(savedAttempt.isCorrect());
        assertTrue(response.correct());
    }
}

package com.blanchaert.quizle.domain.question;

import com.blanchaert.quizle.domain.quiz.QuestionSet;
import com.blanchaert.quizle.domain.quiz.QuestionSetQuestionType;
import com.blanchaert.quizle.domain.quiz.QuestionSetRepository;
import com.blanchaert.quizle.domain.quiz.QuestionSetService;
import com.blanchaert.quizle.domain.user.Role;
import com.blanchaert.quizle.domain.user.User;
import com.blanchaert.quizle.domain.user.UserRepository;
import com.blanchaert.quizle.dto.quiz.CreateQuestionSetQuestionRequest;
import com.blanchaert.quizle.dto.quiz.CreateQuestionSetRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class QuestionSetServiceUnitTest {

    private QuestionSetRepository questionSetRepository;
    private BasicQuestionRepository basicQuestionRepository;
    private SetAnswerQuestionRepository setAnswerQuestionRepository;
    private UserRepository userRepository;
    private QuestionSetService questionSetService;
    private User user;

    @BeforeEach
    void setUp() {
        questionSetRepository = mock(QuestionSetRepository.class);
        basicQuestionRepository = mock(BasicQuestionRepository.class);
        setAnswerQuestionRepository = mock(SetAnswerQuestionRepository.class);
        userRepository = mock(UserRepository.class);
        questionSetService = new QuestionSetService(
                questionSetRepository,
                basicQuestionRepository,
                setAnswerQuestionRepository,
                userRepository
        );

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("teacher");
        user.setEmail("teacher@example.com");
        user.setPasswordHash("hash");
        user.setRole(Role.USER);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("teacher", "password", List.of())
        );
        when(userRepository.findByUsername("teacher")).thenReturn(Optional.of(user));
        when(basicQuestionRepository.save(any(BasicQuestion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(setAnswerQuestionRepository.save(any(SetAnswerQuestion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(questionSetRepository.save(any(QuestionSet.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createQuestionSet_savesOrderedMixedQuestionsForAuthenticatedUser() {
        CreateQuestionSetRequest request = new CreateQuestionSetRequest();
        request.setTitle(" OSI model ");
        request.setDescription(" Networking basics ");
        request.setQuestions(List.of(
                basicQuestion(" In what layer of the OSI model resides TCP? ", " 4 "),
                setAnswerQuestion(
                        "Give me all layers of the OSI model.",
                        7,
                        List.of("Physical", "Data Link", "Network", "Transport", "Session", "Presentation", "Application")
                )
        ));

        questionSetService.createQuestionSet(request);

        ArgumentCaptor<QuestionSet> questionSetCaptor = ArgumentCaptor.forClass(QuestionSet.class);
        verify(questionSetRepository).save(questionSetCaptor.capture());
        QuestionSet savedSet = questionSetCaptor.getValue();

        assertEquals("OSI model", savedSet.getTitle());
        assertEquals("Networking basics", savedSet.getDescription());
        assertEquals(user, savedSet.getCreatedBy());
        assertEquals(2, savedSet.getQuestions().size());
        assertEquals(1, savedSet.getQuestions().getFirst().getPosition());
        assertEquals(QuestionSetQuestionType.BASIC, savedSet.getQuestions().getFirst().getQuestionType());
        assertEquals("In what layer of the OSI model resides TCP?", savedSet.getQuestions().getFirst().getBasicQuestion().getQuestion());
        assertEquals("4", savedSet.getQuestions().getFirst().getBasicQuestion().getAnswer());
        assertEquals(2, savedSet.getQuestions().get(1).getPosition());
        assertEquals(QuestionSetQuestionType.SET_ANSWER, savedSet.getQuestions().get(1).getQuestionType());
        assertEquals(7, savedSet.getQuestions().get(1).getSetAnswerQuestion().getRequiredAnswers());
    }

    @Test
    void createQuestionSet_rejectsSetAnswerQuestionWhenRequiredAnswersExceedAvailableAnswers() {
        CreateQuestionSetRequest request = new CreateQuestionSetRequest();
        request.setTitle("Broken quiz");
        request.setQuestions(List.of(setAnswerQuestion("Name layers", 3, List.of("Physical", "Data Link"))));

        assertThrows(IllegalArgumentException.class, () -> questionSetService.createQuestionSet(request));
    }

    private CreateQuestionSetQuestionRequest basicQuestion(String question, String answer) {
        CreateQuestionSetQuestionRequest request = new CreateQuestionSetQuestionRequest();
        request.setType(QuestionSetQuestionType.BASIC);
        request.setQuestion(question);
        request.setAnswer(answer);
        return request;
    }

    private CreateQuestionSetQuestionRequest setAnswerQuestion(String question, int requiredAnswers, List<String> answers) {
        CreateQuestionSetQuestionRequest request = new CreateQuestionSetQuestionRequest();
        request.setType(QuestionSetQuestionType.SET_ANSWER);
        request.setQuestion(question);
        request.setRequiredAnswers(requiredAnswers);
        request.setAnswers(answers);
        return request;
    }
}

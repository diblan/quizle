package com.blanchaert.quizle.domain.user;

import com.blanchaert.quizle.dto.UserRegistrationRequest;
import com.blanchaert.quizle.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class UserServiceUnitTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerUser_successfullySavesNewUser() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("johndoe");
        request.setEmail("john@example.com");
        request.setPassword("securePass123");

        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("securePass123")).thenReturn("hashedPassword");

        // When
        userService.registerUser(request);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("johndoe", savedUser.getUsername());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getPasswordHash());
    }

    @Test
    void registerUser_throwsExceptionWhenUsernameExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("existinguser");
        request.setEmail("new@example.com");
        request.setPassword("123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                userService.registerUser(request)
        );

        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_throwsExceptionWhenEmailExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                userService.registerUser(request)
        );

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_checksEmailExistenceAfterNormalization() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("anotheruser");
        request.setEmail(" Existing@Example.com \n");
        request.setPassword("123");

        when(userRepository.existsByUsername("anotheruser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                userService.registerUser(request)
        );

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}

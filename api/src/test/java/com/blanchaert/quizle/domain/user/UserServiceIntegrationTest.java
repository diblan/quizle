package com.blanchaert.quizle.domain.user;

import com.blanchaert.quizle.dto.UserRegistrationRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@SpringBootTest
@Transactional // Rolls back after each test
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_NormalizesEmailAndPersistsLowercase() {
        var request = new UserRegistrationRequest("johndoe", " John.Doe@Example.COM ", "secret");
        userService.registerUser(request);

        User user = userRepository.findByUsername("johndoe").orElseThrow();
        assertEquals("john.doe@example.com", user.getEmail());
    }
}

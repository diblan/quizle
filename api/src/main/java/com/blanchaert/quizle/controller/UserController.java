package com.blanchaert.quizle.controller;

import com.blanchaert.quizle.domain.user.UserService;
import com.blanchaert.quizle.dto.UserRegistrationRequest;
import com.blanchaert.quizle.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/register")
@CrossOrigin // Optional: for development if frontend is hosted separately
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}

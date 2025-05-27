package com.blanchaert.quizle.web;

import com.blanchaert.quizle.exception.UserAlreadyExistsException;
import com.blanchaert.quizle.domain.user.UserService;
import com.blanchaert.quizle.dto.UserRegistrationRequest;
import com.blanchaert.quizle.mapper.UserRegistrationMapper;
import com.blanchaert.quizle.web.form.RegisterForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserRegistrationRequest());
        }
        return "register"; // maps to templates/register.html
    }

    @PostMapping("/register")
    public String handleRegisterForm(
            @Valid @ModelAttribute("user") RegisterForm form,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        // Map to core DTO
        UserRegistrationRequest request = UserRegistrationMapper.fromForm(form);

        try {
            userService.registerUser(request);
            model.addAttribute("success", true);
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "register";
    }
}

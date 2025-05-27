package com.blanchaert.quizle.mapper;

import com.blanchaert.quizle.dto.UserRegistrationRequest;
import com.blanchaert.quizle.web.form.RegisterForm;

public class UserRegistrationMapper {
    public static UserRegistrationRequest fromForm(RegisterForm form) {
        var dto = new UserRegistrationRequest();
        dto.setUsername(form.getUsername());
        dto.setEmail(form.getEmail());
        dto.setPassword(form.getPassword());
        return dto;
    }
}

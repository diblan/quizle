package com.blanchaert.quizle.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Data;

@Data
@Named
@RequestScoped
public class HelloBean {
    private String name;
    private String message;

    public void sayHello() {
        message = "Hello, " + name + "!";
    }
}

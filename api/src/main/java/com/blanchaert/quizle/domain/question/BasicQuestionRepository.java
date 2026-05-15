package com.blanchaert.quizle.domain.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BasicQuestionRepository extends JpaRepository<BasicQuestion, UUID> {
}

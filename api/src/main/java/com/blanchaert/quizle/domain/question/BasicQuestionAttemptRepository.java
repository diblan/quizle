package com.blanchaert.quizle.domain.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BasicQuestionAttemptRepository extends JpaRepository<BasicQuestionAttempt, UUID> {
}

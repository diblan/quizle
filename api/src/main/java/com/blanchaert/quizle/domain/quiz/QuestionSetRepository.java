package com.blanchaert.quizle.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionSetRepository extends JpaRepository<QuestionSet, UUID> {
}

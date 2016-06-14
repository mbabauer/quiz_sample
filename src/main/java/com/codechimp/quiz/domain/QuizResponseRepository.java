package com.codechimp.quiz.domain;

import com.codechimp.quiz.entities.QuizResponse;
import org.springframework.data.repository.CrudRepository;

/**
 * CRUD Repository for QuizResponse
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
public interface QuizResponseRepository extends CrudRepository<QuizResponse, Long> {
    QuizResponse findByEmail(String email);
}

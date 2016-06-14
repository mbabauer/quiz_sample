package com.codechimp.quiz.domain;

import com.codechimp.quiz.entities.Answer;
import org.springframework.data.repository.CrudRepository;

/**
 * CRUD Repository for Answers
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
public interface AnswerRepository extends CrudRepository<Answer, Long> {
}

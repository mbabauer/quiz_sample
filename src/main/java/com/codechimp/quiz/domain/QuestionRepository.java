package com.codechimp.quiz.domain;

import com.codechimp.quiz.entities.Question;
import org.springframework.data.repository.CrudRepository;

/**
 * CRUD Repository for Questions
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
public interface QuestionRepository extends CrudRepository<Question, Long> {
}

package com.codechimp.quiz.domain;

import com.codechimp.quiz.entities.Question;
import com.codechimp.quiz.entities.QuestionResponse;
import org.springframework.data.repository.CrudRepository;

/**
 * CRUD Repository for QuestionResponses
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
public interface QuestionResponseRepository extends CrudRepository<QuestionResponse, Long> {

}

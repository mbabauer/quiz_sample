package com.codechimp.quiz.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Question Response entity
 */
@Entity
@Getter @Setter
public class QuestionResponse {

    /**
     * Default constructor
     */
    public QuestionResponse() { }

    /**
     * Constructor
     * @param question The question
     * @param answer The answer
     */
    public QuestionResponse(Question question, Answer answer) {
        this();
        this.question = question;
        this.answer = answer;
    }

    /**
     * Constructor
     * @param question The question
     */
    public QuestionResponse(Question question) {
        this(question, null);
    }

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    @NotNull
    private Question question;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    public Answer getCorrectAnswer() {
        Answer answer = null;

        for (Answer ans : question.getAnswers()) {
            if (ans.isCorrectAnswer()) {
                answer = ans;
                break;
            }
        }

        return answer;
    }
}

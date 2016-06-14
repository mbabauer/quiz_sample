package com.codechimp.quiz.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Answer entity
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
@Entity
@Getter @Setter
public class Answer implements Serializable {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    @NotNull
    private String displayText;

    @Column
    private boolean correctAnswer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Question question;
}

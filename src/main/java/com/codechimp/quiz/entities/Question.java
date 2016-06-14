package com.codechimp.quiz.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Question Entity
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
@Entity
@Getter @Setter
public class Question implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    @NotNull
    private String prompt;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Answer> answers;
}

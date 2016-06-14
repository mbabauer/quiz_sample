package com.codechimp.quiz.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Quiz Response entity
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
@Entity
@Getter @Setter
public class QuizResponse {


    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false, unique = true)
    @NotNull
    @Email
    private String email;

    @Column
    private boolean completed;

    @OneToMany(fetch = FetchType.EAGER)
    private List<QuestionResponse> responses;
}

package com.kamilla.deppplom.question.repository;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Table
@Entity
@Data
class QuestionEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private int disciplineId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String type;

    @NotBlank
    @Column(nullable = false)
    private String body;

}
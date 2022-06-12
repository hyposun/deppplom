package com.kamilla.deppplom.tests.repository.model;

import lombok.Data;

import javax.persistence.*;

@Table
@Entity
@Data
public class TestEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int disciplineId;

    @Column(nullable = false)
    private float minimumPoints;

    @Column(nullable = false)
    private int lowQuestions;

    @Column(nullable = false)
    private int mediumQuestion;

    @Column(nullable = false)
    private int highQuestions;

}
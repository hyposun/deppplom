package com.kamilla.deppplom.examination.repository;

import lombok.Data;

import javax.persistence.*;

@Table
@Entity
@Data
public class QuestionExaminationEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private int questionId;

    @Column
    private String answer;

    @Column
    private float resultPoints;

    @Column
    private int groupExaminationId;

}

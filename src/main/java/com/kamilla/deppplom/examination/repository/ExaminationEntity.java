package com.kamilla.deppplom.examination.repository;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Data
public class ExaminationEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private int studentId;

    @Column(nullable = false)
    private int testId;

    @Column(nullable = false)
    private int testVersionId;

    @Column(nullable = false)
    private ZonedDateTime started;

    @Column(nullable = true)
    private ZonedDateTime finished;

    @Column(nullable = true)
    private float points;

    @Column(nullable = false)
    private int groupExaminationId;

    @Column(nullable = false)
    @OneToMany(fetch = FetchType.EAGER)
    private List<QuestionExaminationEntity> resultList = new ArrayList<>();

    @Column
    private boolean success;

}

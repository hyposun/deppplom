package com.kamilla.deppplom.tests.repository.model;

import com.kamilla.deppplom.question.repository.QuestionEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Table
@Entity
public class TestVersionEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private int testId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> questions;

}

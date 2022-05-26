package com.kamilla.deppplom.tests;

import com.kamilla.deppplom.question.model.Question;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Table
@Entity
@Data
public class Test {
    @Id
    @GeneratedValue
    private int id;

    private String title;

    private List<Question> questions;

    private int disciplineId;


}

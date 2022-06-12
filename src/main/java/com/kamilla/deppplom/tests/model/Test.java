package com.kamilla.deppplom.tests.model;

import com.kamilla.deppplom.discipline.Discipline;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Test {
    private int id;
    private String title;
    private float minimumPoints;
    private List<TestVersion> versions;
    private Discipline discipline;
    private int lowQuestions;
    private int mediumQuestion;
    private int highQuestions;
}

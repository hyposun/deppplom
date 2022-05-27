package com.kamilla.deppplom.tests.model;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Test {
    private int id;
    private String title;
    private int minimumPoints;
    private List<TestVersion> versions;
    private Discipline discipline;
}

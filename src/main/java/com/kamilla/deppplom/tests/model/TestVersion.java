package com.kamilla.deppplom.tests.model;

import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TestVersion {
    private int id;
    private List<Question> questions;
}

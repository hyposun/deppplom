package com.kamilla.deppplom.question.impl.orderedclosedquestion;

import com.kamilla.deppplom.question.model.Selection;
import lombok.Data;

import java.util.List;

@Data
public class OrderClosedQuestionSelection implements Selection {

    private List<Integer> selectedOptions;

}

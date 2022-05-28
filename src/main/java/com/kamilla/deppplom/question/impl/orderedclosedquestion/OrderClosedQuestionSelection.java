package com.kamilla.deppplom.question.impl.orderedclosedquestion;

import com.kamilla.deppplom.question.model.Selection;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderClosedQuestionSelection implements Selection {

    private List<Integer> selectedOptions;

    @Override
    public String getString() {
        return selectedOptions.stream()
                .map(it -> Integer.toString(it))
                .collect(Collectors.joining(", "));
    }

}

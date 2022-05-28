package com.kamilla.deppplom.question.impl.closedquestion;

import com.kamilla.deppplom.question.model.Selection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ClosedQuestionSelection implements Selection {
    private Set<Integer> selectedOptions;

    @Override
    public String getString() {
        return selectedOptions.stream()
                .map(it -> Integer.toString(it))
                .collect(Collectors.joining(", "));
    }
}

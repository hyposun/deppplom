package com.kamilla.deppplom.question.impl.closedquestion;

import com.kamilla.deppplom.question.model.Selection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class ClosedQuestionSelection implements Selection {
    private Set<Integer> selectedOptions;
}

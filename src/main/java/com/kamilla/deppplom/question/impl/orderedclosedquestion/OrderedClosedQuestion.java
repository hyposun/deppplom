package com.kamilla.deppplom.question.impl.orderedclosedquestion;

import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class OrderedClosedQuestion extends Question<OrderClosedQuestionSelection> {

    public static final String TYPE = "ordered_closed_question";

    private List<Option> options;
    private List<Integer> validOrderedOptions;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public CheckResult check(OrderClosedQuestionSelection selection) {

        float falsePoints = 0.0F;
        List<Integer> selected = selection.getSelectedOptions();
        if (selected.size() != validOrderedOptions.size()) {
            return new CheckResult(falsePoints, resultDescription);
        }

        for (int i = 0; i < selected.size(); i++) {
            int actual = selected.get(i);
            int expected = validOrderedOptions.get(i);
            if (expected != actual) {
                return new CheckResult(falsePoints, resultDescription);
            }
        }

        var rightPoints = getCost();
        return new CheckResult(rightPoints, resultDescription);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        private int id;
        private String title;
    }

}
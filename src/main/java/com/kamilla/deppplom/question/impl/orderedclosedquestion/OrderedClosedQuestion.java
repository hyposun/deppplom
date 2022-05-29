package com.kamilla.deppplom.question.impl.orderedclosedquestion;

import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderedClosedQuestion extends Question {

    private List<Option> options = new ArrayList<>();
    private List<Integer> validOrderedOptions = new ArrayList<>();

    @Override
    public QuestionType getType() {
        return QuestionType.CLOSED_ORDERED;
    }

    @Override
    public CheckResult check(Object selection) {

        var actualSelection = (OrderClosedQuestionSelection) selection;

        float falsePoints = 0.0F;
        List<Integer> selected = actualSelection.getSelectedOptions();
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
package com.kamilla.deppplom.question.impl.orderedclosedquestion;

import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Result;
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
    public Result check(OrderClosedQuestionSelection selection) {

        List<Integer> selected = selection.getSelectedOptions();
        if (selected.size() != validOrderedOptions.size()) {
            return new Result(false, resultDescription);
        }

        for (int i = 0; i < selected.size(); i++) {
            int actual = selected.get(i);
            int expected = validOrderedOptions.get(i);
            if (expected != actual) {
                return new Result(false, resultDescription);
            }
        }

        return new Result(true, resultDescription);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        private int id;
        private String title;
    }

}
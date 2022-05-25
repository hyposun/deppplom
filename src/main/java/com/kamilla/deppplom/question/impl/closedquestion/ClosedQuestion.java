package com.kamilla.deppplom.question.impl.closedquestion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ClosedQuestion extends Question<ClosedQuestionSelection> {

    public static final String TYPE = "closed_question";

    private List<ClosedQuestion.Option> options;
    private Set<Integer> validOptions;

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }

    @Override
    public Result check(ClosedQuestionSelection selection) {
        boolean valid = validOptions.containsAll(selection.getSelectedOptions());
        return new Result(valid, resultDescription);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        private int id;
        private String title;
    }

}

package com.kamilla.deppplom.question.impl.closedquestion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public CheckResult check(ClosedQuestionSelection selection) {

        var selectedValidOptions = selection.getSelectedOptions().stream()
                .filter(it -> validOptions.contains(it))
                .collect(Collectors.toSet());

        float result = (float) getCost() / (float) validOptions.size() * (float) selectedValidOptions.size();

        return new CheckResult(result, resultDescription);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        private int id;
        private String title;
    }

}

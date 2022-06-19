package com.kamilla.deppplom.question.impl.closedquestion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
public class ClosedQuestion extends Question {

    private List<ClosedQuestion.Option> options = new ArrayList<>();

    @Override
    @JsonIgnore
    public QuestionType getType() {
        return QuestionType.CLOSED;
    }

    @Override
    public CheckResult check(Object selection){

        var actualSelection = (ClosedQuestionSelection) selection;
        var validOptions = getValidOptions();

        var selectedValidOptions = actualSelection.getSelectedOptions().stream()
                .filter(validOptions::contains)
                .collect(Collectors.toSet());


        var wrongOptions = actualSelection.getSelectedOptions()
                .stream()
                .filter(item -> !validOptions.contains(item))
                .collect(Collectors.toSet());

        float falsePoints = 0.0F;
        if (!options.isEmpty() && options.size() == selectedValidOptions.size()) {
            return new CheckResult(falsePoints, getExplanation());
        }

        var result = (float)selectedValidOptions.size() / ((float) validOptions.size() + (float) wrongOptions.size());

        return new CheckResult(result, getExplanation());
    }

    private String getExplanation() {
        if (!isBlank(resultDescription)) return resultDescription;
        return options.stream()
                .filter(Option::isValid)
                .map(Option::getTitle)
                .collect(Collectors.joining(", ", "Правильные ответы: ", ""));
    }

    private Set<Integer> getValidOptions() {
        return options.stream()
                .filter(Option::isValid)
                 .map(Option::getId)
                .collect(Collectors.toSet());
        //return options.stream().map(Option::getId).collect(Collectors.toSet());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option {
        private int id;
        private String title;
        private boolean valid;
        private Integer imageMediaId;
    }

}

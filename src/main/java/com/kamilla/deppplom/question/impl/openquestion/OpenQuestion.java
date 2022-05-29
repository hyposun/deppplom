package com.kamilla.deppplom.question.impl.openquestion;

import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.QuestionType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

@Data
public class OpenQuestion extends Question {

    private String validAnswer;
    private float maxDeviation = (float) 0.05;
    private float falsePoints = 0.0F;

    @Override
    public QuestionType getType() {
        return QuestionType.OPENED;
    }

    @Override
    public CheckResult check(Object selection) {
        var actualSelection = (OpenedQuestionSelection) selection;

        String selected = actualSelection.getSelectionString();

        if(StringUtils.isBlank(selected) || StringUtils.isEmpty(selected)) return new CheckResult(falsePoints,resultDescription);

        var distance = LevenshteinDistance.getDefaultInstance();
        var permutations = distance.apply(validAnswer,selected);

        var deviation = (float)permutations / (float)validAnswer.length();

        if (deviation >= maxDeviation) return new CheckResult(falsePoints,resultDescription);

        var resultPoint = getCost();

        return new CheckResult(resultPoint, resultDescription);
        
    }
}

package com.kamilla.deppplom.question.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonSubTypes({
    @JsonSubTypes.Type(value = ClosedQuestion.class),
    @JsonSubTypes.Type(value = OrderedClosedQuestion.class),
})
public abstract class Question {

    protected int id;

    protected String title;

    protected String description;

    protected Difficulty difficulty;

    protected int disciplineId;

    protected String resultDescription;

    protected boolean disabled;

    public abstract QuestionType getType();

    public abstract CheckResult check(Object selection);

    protected int getCost() {
        switch (difficulty) {
            case LOW: return 1;
            case MEDIUM: return 2;
            case HIGH: return 3;
            default: return 0;
        }
    }

}


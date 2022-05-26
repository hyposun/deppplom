package com.kamilla.deppplom.question.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonSubTypes({
    @JsonSubTypes.Type(value = ClosedQuestion.class),
})
public abstract class Question<T extends Selection> {

    protected int id;

    protected String title;

    protected String description;

    protected Difficulty difficulty;

    protected int discipline_id;

    protected String resultDescription;

    public abstract String getType();

    public abstract Result check(T selection);

}
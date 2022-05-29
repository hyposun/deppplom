package com.kamilla.deppplom.question.model;

import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;

public enum QuestionType {

    CLOSED("closed_question", "Закрытый вопрос", ClosedQuestion.class),
    CLOSED_ORDERED("ordered_closed_question", "Закрытый упорядоченный вопрос", OrderedClosedQuestion.class);

    private final String type;
    private final String title;
    private Class<? extends Question> clazz;

    QuestionType(String type, String title, Class<? extends Question> clazz) {
        this.type = type;
        this.title = title;
        this.clazz = clazz;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends Question> getClazz() {
        return clazz;
    }

    public static QuestionType getByType(String type) {
        for (QuestionType value : values()) {
            if (value.type.equals(type)) return value;
        }
        throw new IllegalArgumentException("Неизвестный тип вопроса");
    }

}

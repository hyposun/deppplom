package com.kamilla.deppplom.question.model;

public enum Difficulty {

    LOW("Легкая"),
    MEDIUM("Средняя"),
    HIGH("Высокая");

    private String title;

    Difficulty(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

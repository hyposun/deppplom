package com.kamilla.deppplom.report.model;

public enum GroupExaminationStatus {

    PLANNED("Запланировано"),
    IN_PROCESS("В процессе"),
    FINISHED("Завершено");

    private String title;

    GroupExaminationStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

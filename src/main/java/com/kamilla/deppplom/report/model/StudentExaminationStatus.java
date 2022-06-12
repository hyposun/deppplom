package com.kamilla.deppplom.report.model;

public enum StudentExaminationStatus {

    PLANNED("Запланировано", true),
    NOT_STARTED("Ожидает прохождения", true),
    IN_PROCESS("В процессе", true),
    SUCCESSFUL("Успешно", false),
    FAILED("Неуспешно", false);

    private String title;
    private boolean active;

    StudentExaminationStatus(String title, boolean active) {
        this.title = title;
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }
}

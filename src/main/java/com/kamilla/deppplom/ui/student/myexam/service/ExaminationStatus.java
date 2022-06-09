package com.kamilla.deppplom.ui.student.myexam.service;

public enum ExaminationStatus {

    PLANNED("Запланировано", true),
    NOT_STARTED("Ожидает прохождения", true),
    IN_PROCESS("В процессе", true),
    SUCCESSFUL("Успешно", false),
    FAILED("Неуспешно", false);

    private String title;
    private boolean active;

    ExaminationStatus(String title, boolean active) {
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

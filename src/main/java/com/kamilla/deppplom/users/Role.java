package com.kamilla.deppplom.users;

public enum Role {

    ADMIN("Администратор"),
    TEACHER("Преподаватель"),
    STUDENT("Студент");

    private String title;

    Role(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}

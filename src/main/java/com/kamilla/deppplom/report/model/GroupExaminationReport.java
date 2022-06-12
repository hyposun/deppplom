package com.kamilla.deppplom.report.model;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.users.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupExaminationReport {

    int id;

    StudentGroup group;

    Discipline discipline;

    User teacher;

    Test test;

    LocalDateTime from;

    LocalDateTime to;

    List<StudentExaminationReport> reports;

    int finishedStudentsQuantity;

    float averagePoints;

    GroupExaminationStatus status;

    public boolean isActual() {
        return to.isAfter(LocalDateTime.now());
    }

}
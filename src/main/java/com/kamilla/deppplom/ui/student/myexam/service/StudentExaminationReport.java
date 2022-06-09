package com.kamilla.deppplom.ui.student.myexam.service;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.users.User;
import lombok.Data;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

@Data
public class StudentExaminationReport {

    int groupExaminationId;

    StudentGroup group;

    Discipline discipline;

    User teacher;

    User student;

    Test test;

    ExaminationStatus status;

    LocalDateTime from;

    LocalDateTime to;

    @Nullable
    StudentExamination studentExamination;

}

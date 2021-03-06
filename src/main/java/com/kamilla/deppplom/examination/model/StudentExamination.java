package com.kamilla.deppplom.examination.model;

import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.tests.model.TestVersion;
import com.kamilla.deppplom.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamination {

    int id;

    User student;

    Test test;

    List<QuestionExamination> resultList;

    ZonedDateTime started;

    ZonedDateTime finished;

    float points;

    int groupExaminationId;

    TestVersion testVersion;

}

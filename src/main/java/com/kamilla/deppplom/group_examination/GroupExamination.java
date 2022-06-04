package com.kamilla.deppplom.group_examination;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.tests.model.Test;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupExamination {

    private int id;

    private Test test;

    private StudentGroup group;

    private ZonedDateTime openExamTime;

    private ZonedDateTime  closeExamTime;

}
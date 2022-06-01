package com.kamilla.deppplom.group_examination.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class GroupExaminationRequest {

    private int testId;

    private int groupId;

    private ZonedDateTime openExamTime;

    private ZonedDateTime  closeExamTime;


}

package com.kamilla.deppplom.group_examination.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GroupExaminationRequest {

    private int testId;

    private int groupId;

    private LocalDateTime openExamTime;

    private LocalDateTime closeExamTime;


}

package com.kamilla.deppplom.group_examination.repository.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Table
@Entity
@Data
public class GroupExaminationEntity {
    @Id
    @GeneratedValue
    private int id;

    private int groupId;

    private int testId;

    private LocalDateTime openExamTime;

    private LocalDateTime closeExamTime;

}

package com.kamilla.deppplom.group_examination.repository.model;

import com.kamilla.deppplom.users.User;
import lombok.Data;

import javax.persistence.*;
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

    @ManyToOne
    private User teacher;

}

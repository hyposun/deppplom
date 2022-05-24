package com.kamilla.deppplom.groups;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table
@Data
public class StudentGroup {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    @NotBlank
    private String title;

}

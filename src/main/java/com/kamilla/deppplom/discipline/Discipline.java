package com.kamilla.deppplom.discipline;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Table
@Entity
@Data
public class Discipline {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column
    private int parentId;

}
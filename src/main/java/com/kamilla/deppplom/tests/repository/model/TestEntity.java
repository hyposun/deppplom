package com.kamilla.deppplom.tests.repository.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Data
public class TestEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int disciplineId;

    @Column(nullable = false)
    private int minimumPoints;

    @OneToMany
    private List<TestVersionEntity> versions = new ArrayList<>();

}
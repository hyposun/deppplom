package com.kamilla.deppplom.tests.model;

import lombok.Data;

import java.util.List;

@Data
public class CreateTestRequest {

    private String title;
    private int disciplineId;
    private List<Integer> questionIds;
    private int minimumPoints;

}

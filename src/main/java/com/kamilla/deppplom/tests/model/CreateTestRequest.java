package com.kamilla.deppplom.tests.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class CreateTestRequest {

    @NotBlank
    private String title;

    @Positive
    private int disciplineId;

    @Positive
    private int minimumPoints;

}

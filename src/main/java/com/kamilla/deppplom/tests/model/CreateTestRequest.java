package com.kamilla.deppplom.tests.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
public class CreateTestRequest {

    @NotBlank
    private String title;

    @Positive
    private int disciplineId;

    @Positive
    private float minimumPoints;

    @PositiveOrZero
    private int lowQuestions;

    @PositiveOrZero
    private int mediumQuestion;

    @PositiveOrZero
    private int highQuestions;

}

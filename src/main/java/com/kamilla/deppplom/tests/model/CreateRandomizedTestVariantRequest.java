package com.kamilla.deppplom.tests.model;

import lombok.Data;

@Data
public class CreateRandomizedTestVariantRequest {

    private int testId;
    private int lowQuestions;
    private int mediumQuestion;
    private int highQuestions;
    private int replicas;

}

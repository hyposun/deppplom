package com.kamilla.deppplom.tests.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class ManuallyCreateTestVersionRequest {

    @Positive
    private int testId;

    @NotEmpty
    private List<Integer> questionIds;

}

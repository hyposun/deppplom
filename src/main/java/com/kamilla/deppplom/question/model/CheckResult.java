package com.kamilla.deppplom.question.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CheckResult {

    private float points;

    private String message;

    public float getPoints() {
        return getFormattedPoints(points);
    }

    private float getFormattedPoints(float points) {
        var scale = Math.pow(10, 2);
        var result = Math.ceil(points * scale) / scale;
        return (float) result;
    }

}


package com.kamilla.deppplom.media.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Media {

    private int id;

    @Nullable
    private String key;

    @NotNull
    private String name;

}

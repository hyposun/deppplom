package com.kamilla.deppplom.question.impl.openquestion;

import com.kamilla.deppplom.question.model.Selection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenedQuestionSelection implements Selection {

    String selectionString;

    @Override
    public String getString() {
        return selectionString;
    }
}

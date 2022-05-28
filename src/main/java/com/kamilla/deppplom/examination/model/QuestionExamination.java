package com.kamilla.deppplom.examination.model;

import com.kamilla.deppplom.question.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionExamination {

    int id;

    Question question;

    float points;

    String answer;

}

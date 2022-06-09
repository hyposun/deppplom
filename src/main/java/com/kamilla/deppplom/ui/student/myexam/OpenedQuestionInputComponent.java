package com.kamilla.deppplom.ui.student.myexam;

import com.kamilla.deppplom.examination.StudentExaminationService;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.openquestion.OpenQuestion;
import com.kamilla.deppplom.question.impl.openquestion.OpenedQuestionSelection;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.student.myexam.service.MyExaminationDispatcher;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import javax.annotation.security.RolesAllowed;

@SpringComponent
@UIScope
@Route(value = "my_examination/:examinationId/opened_questions/:questionId", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT"})
public class OpenedQuestionInputComponent extends BaseQuestionInputComponent<OpenQuestion> {

    private TextField answerField = new TextField("Впишите ответ");

    public OpenedQuestionInputComponent(MyExaminationDispatcher dispatcher, QuestionService questionService, StudentExaminationService studentExaminationService) {
        super(dispatcher, questionService, studentExaminationService);
        answerField.setWidthFull();
        questionBodyLayout.add(answerField);
    }

    @Override
    protected void setupState() {
        super.setupState();
        answerField.setValue("");
        answerField.setReadOnly(false);
    }

    @Override
    protected Selection getSelectionOrNull() {
        if (answerField.isEmpty()) return null;
        var selection = new OpenedQuestionSelection();
        selection.setSelectionString(answerField.getValue());
        answerField.setReadOnly(true);
        return selection;
    }
}
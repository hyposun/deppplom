package com.kamilla.deppplom.ui.student.myexam.questions;

import com.kamilla.deppplom.examination.StudentExaminationService;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion.Option;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestionSelection;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.student.myexam.service.MyExaminationDispatcher;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import javax.annotation.security.RolesAllowed;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
@Route(value = "my_examination/:examinationId/closed_questions/:questionId", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT"})
public class ClosedQuestionInputComponent extends BaseQuestionInputComponent<ClosedQuestion> {

    private CheckboxGroup<Option> options = new CheckboxGroup<>();

    public ClosedQuestionInputComponent(MyExaminationDispatcher dispatcher, QuestionService questionService, StudentExaminationService studentExaminationService) {
        super(dispatcher, questionService, studentExaminationService);
        questionBodyLayout.add(options);
        options.setItemLabelGenerator(Option::getTitle);
        options.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
    }

    @Override
    protected void setupState() {
        super.setupState();
        options.setReadOnly(false);
        options.setItems(question.getOptions());
    }

    @Override
    protected Selection getSelectionOrNull() {
        var selected = options
                .getValue().stream()
                .map(Option::getId)
                .collect(Collectors.toSet());

        if (selected.isEmpty()) return null;

        options.setReadOnly(true);
        return new ClosedQuestionSelection(selected);
    }
}

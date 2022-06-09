package com.kamilla.deppplom.ui.teacher.questions.opened;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.openquestion.OpenQuestion;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.teacher.questions.BaseQuestionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.List;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;

@SpringComponent
@UIScope
@Route(value = "questions/:disciplineId/:questionId/open", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class OpenQuestionView extends BaseQuestionView<OpenQuestion> {

    private TextField validAnswer;

    public OpenQuestionView(QuestionService questionService) {
        super(questionService);
    }

    @Override
    protected Class<OpenQuestion> getQuestionType() {
        return OpenQuestion.class;
    }

    @Override
    protected OpenQuestion buildNewQuestion() {
        return new OpenQuestion();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);
        validAnswer.setValue(question.getValidAnswer());
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        super.beforeLeave(event);
        validAnswer.setValue("");
    }

    @Override
    protected void save() {
        if (validAnswer.isEmpty()) {
            errorNotification("Необходимо заполнить правильный ответ", 2);
            return;
        }
        question.setValidAnswer(validAnswer.getValue());
        super.save();
    }

    @Override
    protected List<Component> getAdditionalInputs() {
        validAnswer = new TextField("Правильный ответ");
        setFullWidth(validAnswer);
        return Collections.singletonList(validAnswer);
    }


}

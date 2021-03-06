package com.kamilla.deppplom.ui.teacher.questions.closed;

import com.kamilla.deppplom.media.MediaService;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.teacher.questions.BaseQuestionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;

@SpringComponent
@UIScope
@Route(value = "questions/:disciplineId/:questionId/closed", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class ClosedQuestionView extends BaseQuestionView<ClosedQuestion> {

    private MediaService mediaService;

    protected Button newOptionButton;
    protected VerticalLayout optionsLayout;
    protected List<ClosedQuestionOptionComponent> optionComponents = new ArrayList<>();

    public ClosedQuestionView(QuestionService questionService, MediaService mediaService) {
        super(questionService);
        this.mediaService = mediaService;
    }

    @Override
    protected Class<ClosedQuestion> getQuestionType() {
        return ClosedQuestion.class;
    }

    @Override
    protected ClosedQuestion buildNewQuestion() {
        return new ClosedQuestion();
    }

    @Override
    protected List<Component> getAdditionComponents() {
        newOptionButton = new Button("Добавить ответ", VaadinIcon.PLUS.create());
        newOptionButton.addClickListener(event ->
            addOption(optionComponents.size() + 1, "", false, null)
        );
        optionsLayout = new VerticalLayout(newOptionButton);
        return Collections.singletonList(optionsLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);
        question.getOptions()
                .forEach(it -> addOption(it.getId(), it.getTitle(), it.isValid(), it.getImageMediaId()));
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        super.beforeLeave(event);
        optionComponents.forEach(it -> optionsLayout.remove(it));
        optionComponents.clear();
    }

    @Override
    protected void save() {
        var possibleAnswers = optionComponents.stream()
                                                            .map(ClosedQuestionOptionComponent::getOption)
                                                            .collect(Collectors.toList());

        var validAnswers = 0;
        var invalidAnswers = 0;
        for (ClosedQuestionOptionComponent.Option answer : possibleAnswers)
        {
            if (answer.isEmpty()) {
                errorNotification("Заполните все ответы, либо удалите лишние", 3);
                return;
            }

            if (answer.isValid()) {
                validAnswers++;
            } else {
                invalidAnswers++;
            }
        }

        if (invalidAnswers == 0 || validAnswers == 0) {
            errorNotification("У вопроса должен быть хотя бы по одному правильному и неправильному ответу", 3);
            return;
        }

        List<ClosedQuestion.Option> options = possibleAnswers.stream()
                                                             .map(option -> new ClosedQuestion.Option(option.getId(), option.getTitle(), option.isValid(), option.getImageMediaId()))
                                                             .collect(Collectors.toList());
        question.setOptions(options);
        super.save();
    }

    private void addOption(int id, String title, boolean valid, Integer imageMediaId) {
        ClosedQuestionOptionComponent component = new ClosedQuestionOptionComponent(id, title, valid, imageMediaId, mediaService);
        optionsLayout.add(component);
        optionComponents.add(component);
        component.setOnDelete(() -> {
            optionsLayout.remove(component);
            optionComponents.remove(component);
        });
    }

    @Data
    @AllArgsConstructor
    public static class Option {
        private int id;
        private String title;
        private boolean valid;
    }

}
package com.kamilla.deppplom.ui.questions.closed;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.QuestionType;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.questions.QuestionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minidev.json.writer.BeansMapper;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@SpringComponent
@UIScope
@Route(value = "questions/:disciplineId/:questionId/closed_question", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class ClosedQuestionView extends VerticalLayout implements BeforeEnterObserver {

    private QuestionService questionService;
    private ClosedQuestion question;

    private Binder<ClosedQuestion> binder = new Binder<>(ClosedQuestion.class);

    private IntegerField id = new IntegerField("ID");
    private TextField title = new TextField("Название");
    private TextField description = new TextField("Описание");
    private TextField resultDescription = new TextField("Пояснение к ответу");
    private Select<Difficulty> difficulty = new Select<>(Difficulty.values());
    private VerticalLayout inputsLayout = new VerticalLayout(id, title, description, resultDescription, difficulty);

    private Button addOption = new Button("Добавить ответ", VaadinIcon.PLUS.create());
    private List<ClosedQuestionOptionComponent> closedQuestionOptionComponents = new ArrayList<>();
    private VerticalLayout optionsLayout = new VerticalLayout(addOption);

    private Button saveButton = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button deleteButton = new Button("Удалить", VaadinIcon.TRASH.create());
    private Button cancelButton = new Button("Отмена");
    private HorizontalLayout actionsLayout = new HorizontalLayout(saveButton, deleteButton, cancelButton);

    public ClosedQuestionView(QuestionService service) {
        super();
        questionService = service;
        binder.bindInstanceFields(this);
        add(new H2("Редактирование вопроса"));

        add(inputsLayout, optionsLayout, actionsLayout);

        id.setReadOnly(true);
        difficulty.setItemLabelGenerator(Difficulty::getTitle);
        difficulty.setPlaceholder("Укажите сложность");

        setFullWidth(id, title, description, difficulty, resultDescription);
        actionsLayout.setAlignSelf(Alignment.END);

        addOption.addClickListener(event ->
            addOption(closedQuestionOptionComponents.size() + 1, "", false)
        );

        saveButton.addClickListener(event -> save());
        saveButton.getElement().getThemeList().add("primary");
        deleteButton.addClickListener(event -> delete());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event -> cancel());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        question = getQuestion(event);
        binder.setBean(question);
        optionsLayout.removeAll();
        question.getOptions()
                .forEach(it -> addOption(it.getId(), it.getTitle(), it.isValid()));
    }

    private ClosedQuestion getQuestion(BeforeEnterEvent event) {

        RouteParameters parameters = event.getRouteParameters();
        int questionId = parameters.get("questionId").map(NumberUtils::toInt).orElse(0);
        int disciplineId = parameters.get("disciplineId").map(NumberUtils::toInt).get();

        Question question = questionService.findQuestionById(questionId).orElseGet(() -> {
            ClosedQuestion closedQuestion = new ClosedQuestion();
            closedQuestion.setDisciplineId(disciplineId);
            return closedQuestion;
        });

        if (question.getType() != QuestionType.CLOSED) {
            event.getUI().navigate(QuestionView.class);
            errorNotification("Некорректный тип вопроса", 3);
        }

        return (ClosedQuestion) question;
    }

    private void setFullWidth(HasSize ... components) {
        for (HasSize component : components) {
            component.setWidthFull();
        }
    }

    private void save() {

        var possibleAnswers = closedQuestionOptionComponents.stream()
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
                                                             .map(option -> new ClosedQuestion.Option(option.getId(), option.getTitle(), option.isValid()))
                                                             .collect(Collectors.toList());
        question.setOptions(options);

        questionService.save(question);
        successNotification("Изменения сохранены", 2);
    }

    private void delete() {
        if (question.getId() > 0) {
            questionService.deleteById(question.getId());
            successNotification("Вопрос удален", 2);
        }
        getUI().ifPresent(ui -> ui.navigate(QuestionView.class));
    }

    private void cancel() {
        getUI().ifPresent(ui -> ui.navigate(QuestionView.class));
    }

    private void addOption(int id, String title, boolean valid) {
        ClosedQuestionOptionComponent component = new ClosedQuestionOptionComponent(id, title, valid);
        optionsLayout.add(component);
        closedQuestionOptionComponents.add(component);
        component.setOnDelete(() -> {
            optionsLayout.remove(component);
            closedQuestionOptionComponents.remove(component);
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
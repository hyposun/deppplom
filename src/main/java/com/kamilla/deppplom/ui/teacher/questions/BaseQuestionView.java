package com.kamilla.deppplom.ui.teacher.questions;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.question.model.Question;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Collections;
import java.util.List;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@SuppressWarnings("unchecked")
public abstract class BaseQuestionView<T extends Question> extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    protected QuestionService questionService;
    protected T question;
    private Binder<T> binder;

    protected IntegerField id = new IntegerField("ID");
    protected TextField title = new TextField("Название");
    protected TextField description = new TextField("Описание");
    protected TextField resultDescription = new TextField("Пояснение к ответу");
    protected Select<Difficulty> difficulty = new Select<>(Difficulty.values());
    protected VerticalLayout inputsLayout = new VerticalLayout(id, title, description, resultDescription, difficulty);

    protected Button saveButton = new Button("Сохранить", VaadinIcon.CHECK.create());
    protected Button deleteButton = new Button("Удалить", VaadinIcon.TRASH.create());
    protected Button cancelButton = new Button("Отмена");
    protected HorizontalLayout actionsLayout = new HorizontalLayout(saveButton, deleteButton, cancelButton);

    public BaseQuestionView(QuestionService questionService) {
        this.questionService = questionService;
        this.binder = new Binder<>(getQuestionType());
        binder.bindInstanceFields(this);

        add(new H1("Редактирование вопроса"));
        getAdditionalInputs().forEach(inputsLayout::add);
        add(inputsLayout);
        getAdditionComponents().forEach(this::add);
        add(new VerticalLayout(actionsLayout));

        id.setReadOnly(true);
        difficulty.setItemLabelGenerator(Difficulty::getTitle);
        difficulty.setPlaceholder("Выберите сложность");
        setFullWidth(id, title, description, difficulty, resultDescription);

        saveButton.addClickListener(event -> save());
        saveButton.getElement().getThemeList().add("primary");
        deleteButton.addClickListener(event -> delete());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event -> cancel());

    }

    protected List<Component> getAdditionalInputs() {
        return Collections.emptyList();
    }

    protected abstract Class<T> getQuestionType();

    protected abstract T buildNewQuestion();

    protected List<Component> getAdditionComponents() {
        return Collections.emptyList();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        question = getQuestion(event);
        binder.setBean(question);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

    private T getQuestion(BeforeEnterEvent event) {

        RouteParameters parameters = event.getRouteParameters();
        int questionId = parameters.get("questionId").map(NumberUtils::toInt).orElse(0);
        int disciplineId = parameters.get("disciplineId").map(NumberUtils::toInt).orElse(0);

        Question question = questionService.findQuestionById(questionId).orElseGet(() -> {
            T newQuestion = buildNewQuestion();
            newQuestion.setDisciplineId(disciplineId);
            return newQuestion;
        });

        if (question.getClass() != getQuestionType()) {
            navigateToQuestionsView();
            errorNotification("Некорректный тип вопроса", 3);
        }

        return (T) question;
    }

    private void delete() {
        if (question.getId() > 0) {
            questionService.deleteById(question.getId());
            successNotification("Вопрос удален", 2);
        }
        navigateToQuestionsView();
    }

    protected void save() {
        questionService.save(question);
        successNotification("Изменения сохранены", 2);
        navigateToQuestionsView();
    }

    private void cancel() {
        navigateToQuestionsView();
    }

    private void navigateToQuestionsView() {
        getUI().ifPresent(ui -> ui.navigate(QuestionsView.class));
    }

    protected void setFullWidth(HasSize... components) {
        for (HasSize component : components) {
            component.setWidthFull();
        }
    }

}

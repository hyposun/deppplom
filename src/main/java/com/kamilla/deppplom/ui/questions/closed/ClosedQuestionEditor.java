package com.kamilla.deppplom.ui.questions.closed;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.ui.AbstractDialog;
import com.kamilla.deppplom.ui.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kamilla.deppplom.ui.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.UIUtils.successNotification;

@SpringComponent
@UIScope
public class ClosedQuestionEditor extends AbstractDialog {

    private QuestionService questionService;
    private ClosedQuestion question;

    private TextField id = new TextField("ID");
    private TextField title = new TextField("Название");
    private TextField description = new TextField("Описание");
    private TextField explanation = new TextField("Пояснение к ответу");
    private Select<Difficulty> difficulty = new Select<>(Difficulty.values());
    private VerticalLayout optionsLayout = new VerticalLayout();

    private Button addOption = new Button("Добавить ответ", VaadinIcon.PLUS.create());
    private List<ClosedQuestionOptionComponent> closedQuestionOptionComponents = new ArrayList<>();

    public ClosedQuestionEditor(QuestionService service) {
        super();
        questionService = service;
        setupLayout();
        setupOptions();
    }

    public void editQuestion(ClosedQuestion question) {

        reset();

        this.question = question;

        this.id.setValue(String.valueOf(question.getId()));

        if (this.question.getDifficulty() != null) {
            this.difficulty.setValue(this.question.getDifficulty());
        }
        if (this.question.getTitle() != null) {
            this.title.setValue(this.question.getTitle());
        }
        if (this.question.getDescription() != null) {
            this.description.setValue(this.question.getDescription());
        }
        if (this.question.getResultDescription() != null) {
            this.explanation.setValue(question.getResultDescription());
        }


        for (ClosedQuestion.Option option : this.question.getOptions()) {
            addOption(option.getId(),option.getTitle(), option.isValid());
        }

        setVisible(true);
    }



    @Override
    protected void delete() {
        questionService.deleteById(question.getId());
        UIUtils.successNotification("Вопрос удален", 2);
        onClose.run();
        setVisible(false);
    }

    @Override
    protected void save() {

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

        question.setTitle(title.getValue());
        question.setDescription(description.getValue());
        question.setDifficulty(difficulty.getValue());
        question.setResultDescription(explanation.getValue());
        question.setOptions(options);

        questionService.save(question);
        if (question.getId() > 0) {
            successNotification("Вопрос изменен", 2);
        } else {
            successNotification("Вопрос добавлен", 2);
        }

        setVisible(false);
        onClose.run();
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

    private void setupOptions() {
        addOption.addClickListener(event -> addOption(closedQuestionOptionComponents.size() + 1, "", false));
    }

    private void setupLayout() {

        VerticalLayout layout = new VerticalLayout(id, title, description, explanation, difficulty);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setMinWidth("650px");
        layout.setPadding(false);
        layout.setMargin(false);

        id.setReadOnly(true);
        id.setWidthFull();
        title.setWidthFull();
        description.setWidthFull();
        difficulty.setWidthFull();
        explanation.setWidthFull();

        difficulty.setItemLabelGenerator(Difficulty::getTitle);
        difficulty.setPlaceholder("Укажите сложность");

        optionsLayout.add(addOption);
        add(layout, optionsLayout, actions);
        setMinWidth("700px");
        setVisible(false);
    }

    private void reset() {
        onClose.run();
        id.clear();
        title.clear();
        description.clear();
        explanation.clear();
        difficulty.clear();
        question = null;
        closedQuestionOptionComponents.forEach(component -> optionsLayout.remove(component));
        closedQuestionOptionComponents.clear();
    }


    @Data
    @AllArgsConstructor
    public static class Option {
        private int id;
        private String title;
        private boolean valid;
    }

}


package com.kamilla.deppplom.ui.questions.ordered;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.ui.AbstractDialog;
import com.kamilla.deppplom.ui.UIUtils;
import com.kamilla.deppplom.ui.questions.closed.ClosedQuestionOptionComponent;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.kamilla.deppplom.ui.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.UIUtils.successNotification;
import static java.util.Arrays.stream;

@SpringComponent
@UIScope
public class OrderedCloseQuestionEditor extends AbstractDialog {

    private QuestionService questionService;
    private OrderedClosedQuestion question;

    private TextField id = new TextField("ID");
    private TextField title = new TextField("Название");
    private TextField description = new TextField("Описание");
    private TextField explanation = new TextField("Пояснение к ответу");
    private Select<Difficulty> difficulty = new Select<>(Difficulty.values());
    private TextField validOrderIds = new TextField("Номера ответов в правильном порядке");
    private VerticalLayout optionsLayout = new VerticalLayout();

    private Button addOption = new Button("Добавить ответ", VaadinIcon.PLUS.create());
    private List<OrderedQuestionOptionComponent> orderedQuestionOptionComponents = new ArrayList<>();

    public OrderedCloseQuestionEditor(QuestionService service) {
        super();
        questionService = service;
        setupLayout();
        setupOptions();
    }

    public void editQuestion(OrderedClosedQuestion question) {

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
        this.validOrderIds.setValue(question.getValidOrderedOptions().stream()
                                            .map(it -> Integer.toString(it))
                                            .collect(Collectors.joining(",")));


        for (OrderedClosedQuestion.Option option : this.question.getOptions()) {
            addOption(option.getId(),option.getTitle());
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

        var possibleAnswers = orderedQuestionOptionComponents.stream()
                                                             .map(OrderedQuestionOptionComponent::getOption)
                                                             .collect(Collectors.toList());

        for (OrderedQuestionOptionComponent.Option answer : possibleAnswers) {
            if (answer.isEmpty()) {
                errorNotification("Заполните все ответы, либо удалите лишние", 3);
                return;
            }
        }

        String value = validOrderIds.getValue();
        if (value.isEmpty()) {
            errorNotification("Укажите корректный правильный порядок ответов", 2);
            return;
        }

        List<OrderedClosedQuestion.Option> options = possibleAnswers.stream()
                                                         .map(option -> new OrderedClosedQuestion.Option(option.getId(), option.getTitle()))
                                                         .collect(Collectors.toList());

        List<Integer> validOrderedOptions = stream(value.split(",")).sequential()
                                                                    .map(it -> Integer.parseInt(it.trim()))
                                                                    .collect(Collectors.toList());

        boolean illegalIds = !validOrderedOptions.stream()
                                                .allMatch(id -> options.stream()
                                                                       .anyMatch(option -> option.getId() == id));
        if (illegalIds) {
            errorNotification("В правиальном ответе указан ID несуществующего вариант", 3);
            return;
        }

        question.setTitle(title.getValue());
        question.setDescription(description.getValue());
        question.setDifficulty(difficulty.getValue());
        question.setResultDescription(explanation.getValue());
        question.setOptions(options);
        question.setValidOrderedOptions(validOrderedOptions);

        questionService.save(question);

        if (question.getId() > 0) {
            successNotification("Вопрос изменен", 2);
        } else {
            successNotification("Вопрос добавлен", 2);
        }

        setVisible(false);
        onClose.run();
    }

    private void addOption(int id, String title) {
        OrderedQuestionOptionComponent component = new OrderedQuestionOptionComponent(id, title);
        optionsLayout.add(component);
        orderedQuestionOptionComponents.add(component);
        component.setOnDelete(() -> {
            optionsLayout.remove(component);
            orderedQuestionOptionComponents.remove(component);
        });
    }

    private void setupOptions() {
        addOption.addClickListener(event -> addOption(orderedQuestionOptionComponents.size() + 1, ""));
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
        validOrderIds.setWidthFull();

        difficulty.setItemLabelGenerator(Difficulty::getTitle);
        difficulty.setPlaceholder("Укажите сложность");

        optionsLayout.add(addOption);
        add(layout, optionsLayout, validOrderIds, actions);
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
        validOrderIds.clear();
        question = null;
        orderedQuestionOptionComponents.forEach(component -> optionsLayout.remove(component));
        orderedQuestionOptionComponents.clear();
    }


    @Data
    @AllArgsConstructor
    public static class Option {
        private int id;
        private String title;
        private boolean valid;
    }

}


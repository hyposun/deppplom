package com.kamilla.deppplom.ui.teacher.test;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.Test;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@UIScope
@SpringComponent
public class SelectiveTestVersionGenerationDialog extends Dialog {

    private TestService testService;
    private QuestionService questionService;
    private Test test;
    private Runnable onClose;

    private MultiSelectListBox<QuestionItem> questionsListbox = new MultiSelectListBox<>();

    private Button generateButton = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button cancelButton = new Button("Отмена", VaadinIcon.CHECK.create());

    public SelectiveTestVersionGenerationDialog(
        TestService testService,
        QuestionService questionService
    ) {
        this.testService = testService;
        this.questionService = questionService;

        setWidthFull();
        questionsListbox.setWidthFull();
        questionsListbox.setItemLabelGenerator(QuestionItem::getTitle);

        generateButton.getElement().getThemeList().add("primary");
        generateButton.addClickListener(event -> save());
        cancelButton.addClickListener(event -> cancel());

        add(questionsListbox, generateButton, cancelButton);

        setVisible(false);
    }

    public void show(
        Test test,
        Runnable onClose
    ) {
        this.onClose = onClose;
        this.test = test;

        List<QuestionItem> questions = questionService
                .findAllByDisciplineHierarchy(test.getDiscipline().getId()).stream()
                .map(it -> new QuestionItem(it.getId(), it.getTitle() + " [" + it.getDifficulty().getTitle() + "]", it.getDifficulty()))
                .collect(Collectors.toList());

        questionsListbox.setItems(questions);

        setVisible(true);
        open();
    }

    private void cancel() {
        onClose.run();
        close();
    }

    private void save() {
        try {

            Set<QuestionItem> values = questionsListbox.getValue();
            if (!verifyRules(values)) return;

            List<Integer> questions = values.stream()
                    .map(QuestionItem::getId)
                    .collect(Collectors.toList());

            testService.manuallyCreateVersion(test.getId(), questions);
            successNotification("Готово", 1);
            onClose.run();
            close();
        } catch (Exception e) {
            var message = e.getMessage() == null ? e.toString() : e.getMessage();
            errorNotification(message, 2);
        }
    }

    private boolean verifyRules(Set<QuestionItem> values) {

        Map<Difficulty, List<QuestionItem>> grouped = values.stream()
                .collect(Collectors.groupingBy(QuestionItem::getDifficulty));

        boolean valid = true;

        List<QuestionItem> lowQuestions = grouped.get(Difficulty.LOW);
        if (test.getLowQuestions() != lowQuestions.size()) {
            valid = false;
        }

        List<QuestionItem> mediumQuestions = grouped.get(Difficulty.MEDIUM);
        if (test.getMediumQuestion() != mediumQuestions.size()) {
            valid = false;
        }

        List<QuestionItem> highQuestions = grouped.get(Difficulty.HIGH);
        if (test.getHighQuestions() != highQuestions.size()) {
            valid = false;
        }

        if (!valid) {
            StringBuilder builder = new StringBuilder();
            builder.append("Обнаружено несоответсвие условиям теста: ").append(System.lineSeparator());
            builder.append("Легких вопросов - ").append(lowQuestions.size()).append(" из ").append(test.getLowQuestions());
            builder.append("Средних вопросов - ").append(mediumQuestions.size()).append(" из ").append(test.getMediumQuestion());
            builder.append("Сложных вопросов - ").append(highQuestions.size()).append(" из ").append(test.getHighQuestions());
            errorNotification(builder.toString(), 2);
        }

        return valid;
    }

    @Data
    @AllArgsConstructor
    private static class QuestionItem {
        private int id;
        private String title;
        private Difficulty difficulty;
    }

}

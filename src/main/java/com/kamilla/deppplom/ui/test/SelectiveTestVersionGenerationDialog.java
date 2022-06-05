package com.kamilla.deppplom.ui.test;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.ManuallyCreateTestVersionRequest;
import com.kamilla.deppplom.tests.model.Test;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.HashSet;
import java.util.List;
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

    private MultiSelectListBox<Question> questionsListbox = new MultiSelectListBox<>();
    private Set<Question> selectedQuestions = new HashSet<>();

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
        questionsListbox.setItemLabelGenerator(Question::getTitle);
        questionsListbox.addSelectionListener(event -> {
            selectedQuestions.clear();
            selectedQuestions.addAll(event.getAllSelectedItems());
        });

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

        List<Question> questions = questionService.findAllByDisciplineHierarchy(test.getDiscipline().getId());
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
            List<Integer> questions = selectedQuestions.stream()
                    .map(Question::getId)
                    .collect(Collectors.toList());

            ManuallyCreateTestVersionRequest request = new ManuallyCreateTestVersionRequest();
            request.setTestId(test.getId());
            request.setQuestionIds(questions);
            testService.manuallyCreateVersion(request);
            successNotification("Готово", 1);
            onClose.run();
            close();
        } catch (Exception e) {
            var message = e.getMessage() == null ? e.toString() : e.getMessage();
            errorNotification(message, 2);
        }
    }

}
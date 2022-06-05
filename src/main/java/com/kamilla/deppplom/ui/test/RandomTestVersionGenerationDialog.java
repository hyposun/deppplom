package com.kamilla.deppplom.ui.test;

import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.CreateRandomizedTestVariantRequest;
import com.kamilla.deppplom.ui.utils.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@UIScope
@SpringComponent
public class RandomTestVersionGenerationDialog extends Dialog {

    private TestService service;
    private CreateRandomizedTestVariantRequest testVariantRequest;
    private Binder<CreateRandomizedTestVariantRequest> binder = new Binder<>(CreateRandomizedTestVariantRequest.class);
    private Runnable onClose;

    private IntegerField lowQuestions = new IntegerField("Количество легких вопросов");
    private IntegerField mediumQuestion = new IntegerField("Количество средних вопросов");
    private IntegerField highQuestions = new IntegerField("Количество сложных вопросов");
    private IntegerField replicas = new IntegerField("Количество версий для генерации");

    private Button generateButton = new Button("Сгенерировать", VaadinIcon.CHECK.create());
    private Button cancelButton = new Button("Отмена", VaadinIcon.CHECK.create());

    public RandomTestVersionGenerationDialog(
        TestService service
    ) {
        this.service = service;

        lowQuestions.setWidthFull();
        mediumQuestion.setWidthFull();
        highQuestions.setWidthFull();
        replicas.setWidthFull();
        add(lowQuestions, mediumQuestion, highQuestions, replicas);

        generateButton.getElement().getThemeList().add("primary");
        generateButton.addClickListener(event -> generate());
        cancelButton.addClickListener(event -> cancel());

        add(generateButton, cancelButton);
        binder.bindInstanceFields(this);

        setVisible(false);
    }

    public void show(
        int testId,
        Runnable onClose
    ) {
        this.onClose = onClose;
        testVariantRequest = new CreateRandomizedTestVariantRequest();
        testVariantRequest.setTestId(testId);
        binder.setBean(testVariantRequest);
        replicas.setValue(1);
        setVisible(true);
        open();
    }

    private void cancel() {
        onClose.run();
        close();
    }

    private void generate() {
        try {
            service.createRandomizedVariants(testVariantRequest);
            successNotification("Готово", 1);
            onClose.run();
            close();
        } catch (Exception e) {
            var message = e.getMessage() == null ? e.toString() : e.getMessage();
            errorNotification(message, 2);
        }
    }

}

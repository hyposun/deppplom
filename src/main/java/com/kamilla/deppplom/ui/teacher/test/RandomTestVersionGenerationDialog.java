package com.kamilla.deppplom.ui.teacher.test;

import com.kamilla.deppplom.tests.TestService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@UIScope
@SpringComponent
public class RandomTestVersionGenerationDialog extends Dialog {

    private TestService service;
    private Runnable onClose;
    private int testId;

    private IntegerField replicas = new IntegerField("Количество версий для генерации");
    private Button generateButton = new Button("Сгенерировать", VaadinIcon.CHECK.create());
    private Button cancelButton = new Button("Отмена", VaadinIcon.CHECK.create());

    public RandomTestVersionGenerationDialog(
        TestService service
    ) {
        this.service = service;
        replicas.setWidthFull();
        add(replicas);

        generateButton.getElement().getThemeList().add("primary");
        generateButton.addClickListener(event -> generate());
        cancelButton.addClickListener(event -> cancel());

        add(generateButton, cancelButton);

        setVisible(false);
    }

    public void show(
        int testId,
        Runnable onClose
    ) {
        this.onClose = onClose;
        this.testId = testId;
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
            Integer replicas = this.replicas.getValue();
            if (replicas == null || replicas == 0) {
                errorNotification("Укажите количество копий", 2);
                return;
            }

            service.createRandomizedVariants(testId, replicas);
            successNotification("Готово", 1);
            onClose.run();
            close();
        } catch (Exception e) {
            var message = e.getMessage() == null ? e.toString() : e.getMessage();
            errorNotification(message, 2);
        }
    }

}

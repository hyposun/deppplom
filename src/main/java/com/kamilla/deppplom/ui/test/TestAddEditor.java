package com.kamilla.deppplom.ui.test;

import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.CreateTestRequest;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.ui.utils.UIUtils;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@SuppressWarnings("FieldCanBeLocal")
@UIScope
@SpringComponent
public class TestAddEditor extends Dialog implements KeyNotifier {

    private TestService service;
    private int disciplineId;

    @Setter
    protected Runnable onClose;
    private TextField title;
    private NumberField minimumPoints;

    public TestAddEditor(TestService service) {

        this.service = service;
        this.setVisible(false);

        addKeyPressListener(Key.ENTER, event -> save());

        Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
        save.getElement().getThemeList().add("primary");
        save.addClickListener(event -> save());

        Button cancel = new Button("Отмена");
        cancel.addClickListener(event -> setVisible(false));

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setMargin(true);
        actions.setWidthFull();
        actions.setAlignItems(FlexComponent.Alignment.CENTER);

        title = new TextField("Название теста");
        title.setWidthFull();

        minimumPoints = new NumberField("Проходной балл (может быть дробным)");
        minimumPoints.setWidthFull();

        VerticalLayout verticalLayout = new VerticalLayout(title, minimumPoints, actions);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(verticalLayout);

    }


    private void save() {

        if (title.isEmpty() && minimumPoints.isEmpty()) {
            UIUtils.errorNotification("Необходимо заполнит все параметры", 9);
            return;
        }

        Test test;
        try {
            CreateTestRequest request = new CreateTestRequest();
            request.setTitle(title.getValue());
            request.setMinimumPoints(new Float(minimumPoints.getValue()));
            request.setDisciplineId(disciplineId);
            test = service.createTest(request);
        } catch (Exception e) {
            var message = e.getMessage() != null ? e.getMessage() : e.toString();
            errorNotification(message, 2);
            return;
        }

        setVisible(false);

        successNotification("Тест зарегистрирован", 2);
        getUI().ifPresent(ui -> {
            var params = new RouteParameters("testId", String.valueOf(test.getId()));
            ui.navigate(TestEditorView.class, params);
        });

    }

    public void start(int disciplineId) {
        this.disciplineId = disciplineId;
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            open();
        } else {
            if (onClose != null) {
                onClose.run();
            }
            close();
        }
    }

}

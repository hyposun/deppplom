package com.kamilla.deppplom.ui.test;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.CreateTestRequest;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.Optional;

import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;


@UIScope
@SpringComponent
@Route(value = "tests/:disciplineId/:testId", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class TestEditorView extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private TestService testService;
    private DisciplineService disciplineService;
    private Test test;
    private TestVersionsEditor versionsEditor;

    private Binder<Test> binder = new Binder<>(Test.class);

    private IntegerField id = new IntegerField("ID");
    private TextField title = new TextField("Название");
    private TextField points = new TextField("Проходной балл");
    private Select<Discipline> discipline = new Select<>();
    private FormLayout formLayout = new FormLayout(id, discipline, title, points);

    protected Button saveButton = new Button("Сохранить", VaadinIcon.CHECK.create());
    protected Button deleteButton = new Button("Удалить", VaadinIcon.TRASH.create());
    protected Button cancelButton = new Button("Отмена");
    protected HorizontalLayout actions = new HorizontalLayout(saveButton, deleteButton, cancelButton);

    public TestEditorView(TestService testService, DisciplineService disciplineService, TestVersionsEditor versionsEditor) {
        this.testService = testService;
        this.disciplineService = disciplineService;
        this.versionsEditor = versionsEditor;

        formLayout.setColspan(id, 1);
        formLayout.setColspan(discipline, 1);
        formLayout.setColspan(title, 2);
        formLayout.setColspan(points, 2);

        id.setReadOnly(true);
        discipline.setItemLabelGenerator(Discipline::getTitle);
        discipline.setReadOnly(true);

        binder.bindInstanceFields(this);
        binder.forField(id)
                .bind(Test::getId, Test::setId);
        binder.forField(title)
                .bind(Test::getTitle, Test::setTitle);
        binder.forField(points)
                .withConverter(new StringToFloatConverter("Проходной балл должен быть числом"))
                .bind(Test::getMinimumPoints, Test::setMinimumPoints);
        binder.forField(discipline)
                .bind(Test::getDiscipline, Test::setDiscipline);

        add(formLayout, versionsEditor, actions);

        saveButton.addClickListener(event -> save());
        saveButton.getElement().getThemeList().add("primary");
        deleteButton.addClickListener(event -> delete());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event -> cancel());
    }

    private void save() {
        if (test.getId() > 0) return;

        CreateTestRequest request = new CreateTestRequest();
        request.setDisciplineId(test.getDiscipline().getId());
        request.setTitle(test.getTitle());
        request.setMinimumPoints(test.getMinimumPoints());
        test = testService.createTest(request);
        binder.setBean(test);

        successNotification("Тест сохранен", 2);
        setupInputsVisibility();
    }

    private void setupInputsVisibility() {
        if (test.getId() > 0) {
            versionsEditor.show(test.getId());
            title.setReadOnly(true);
            points.setReadOnly(true);
            deleteButton.setVisible(true);
            saveButton.setVisible(false);
        } else {
            versionsEditor.setVisible(false);
            title.setReadOnly(false);
            points.setReadOnly(false);
            saveButton.setVisible(true);
            deleteButton.setVisible(false);
        }
    }

    private void delete() {
        testService.delete(test.getId());
        getUI().ifPresent(ui -> ui.navigate(TestsView.class));
        successNotification("Тест удален", 2);
    }

    private void cancel() {
        getUI().ifPresent(ui -> ui.navigate(TestsView.class));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        test = getTest(event);
        if (test == null) {
            cancel();
            return;
        }
        discipline.setItems(test.getDiscipline());
        binder.setBean(test);
        setupInputsVisibility();
    }

    private Test getTest(BeforeEnterEvent event) {

        RouteParameters parameters = event.getRouteParameters();
        int disciplineId = parameters.get("disciplineId").map(NumberUtils::toInt).orElse(0);
        int testId = parameters.get("testId").map(NumberUtils::toInt).orElse(0);

        if (testId == 0 && disciplineId == 0) {
            return null;
        }

        return testService.findById(testId).orElseGet(() -> {
            Optional<Discipline> discipline = disciplineService.findById(disciplineId);
            if (discipline.isEmpty()) return null;
            return new Test(0, "", 0f, Collections.emptyList(), discipline.get());
        });
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

}

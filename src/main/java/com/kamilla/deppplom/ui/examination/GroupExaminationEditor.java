package com.kamilla.deppplom.ui.examination;

import com.kamilla.deppplom.group_examination.GroupExamination;
import com.kamilla.deppplom.group_examination.service.GroupExaminationService;
import com.kamilla.deppplom.group_examination.service.model.GroupExaminationRequest;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.security.RolesAllowed;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;


@UIScope
@SpringComponent
@Route(value = "examinations/:id", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class GroupExaminationEditor extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private GroupExaminationService service;
    private StudentGroupService groupService;
    private TestService testService;
    private GroupExamination model;

    private Binder<GroupExamination> binder = new Binder<>(GroupExamination.class);
    private IntegerField id = new IntegerField("ID");
    private Select<StudentGroup> group = new Select<>();
    private Select<Test> test = new Select<>();
    private DateTimePicker openExamTime = new DateTimePicker("Начало экзамена");
    private DateTimePicker closeExamTime = new DateTimePicker("Окончание экзамена");

    private Button saveButton = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button cancelButton = new Button("Отмена");

    public GroupExaminationEditor(GroupExaminationService service, StudentGroupService groupService, TestService testService) {
        this.service = service;
        this.groupService = groupService;
        this.testService = testService;

        id.setReadOnly(true);
        id.setWidthFull();
        add(id);

        group.setItems(groupService.findAll());
        group.setPlaceholder("Группа");
        group.setWidthFull();
        group.setItemLabelGenerator(StudentGroup::getTitle);
        add(group);

        test.setItems(testService.findAll());
        test.setPlaceholder("Тест");
        test.setWidthFull();
        test.setItemLabelGenerator(Test::getTitle);
        add(test);

        FormLayout formLayout = new FormLayout();
        openExamTime.setWidthFull();
        formLayout.add(openExamTime, 1);
        closeExamTime.setWidthFull();
        formLayout.add(closeExamTime, 1);
        add(formLayout);

        HorizontalLayout buttonsLayer = new HorizontalLayout(saveButton, cancelButton);
        saveButton.addClickListener(event -> save());
        cancelButton.addClickListener(event -> cancel());
        add(buttonsLayer);

        binder.bindInstanceFields(this);
    }

    private void cancel() {
        getUI().ifPresent(ui -> ui.navigate(GroupExaminationView.class));
    }

    private void save() {

        boolean nonValid = model.getTest() == null || model.getGroup() == null || model.getCloseExamTime() == null || model.getOpenExamTime() == null;
        if (nonValid) {
            errorNotification("Необходимо заполнить все поля", 2);
            return;
        }

        GroupExaminationRequest request = new GroupExaminationRequest(
            model.getTest().getId(),
            model.getGroup().getId(),
            model.getOpenExamTime(),
            model.getCloseExamTime()
        );
        service.startGroupExamination(request);
        successNotification("Экзамен добавлен", 2);
        getUI().ifPresent(ui -> ui.navigate(GroupExaminationView.class));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        model = parameters.get("id")
                .map(NumberUtils::toInt)
                .flatMap(it -> service.findById(it))
                .orElse(new GroupExamination());
        binder.setBean(model);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

}

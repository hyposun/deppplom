package com.kamilla.deppplom.ui.teacher.examination;

import com.kamilla.deppplom.group_examination.GroupExamination;
import com.kamilla.deppplom.group_examination.service.GroupExaminationService;
import com.kamilla.deppplom.group_examination.service.model.GroupExaminationRequest;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.users.User;
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
import java.time.LocalDateTime;
import java.util.Locale;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;


@UIScope
@SpringComponent
@Route(value = "examinations/:id", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class GroupExaminationEditor extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private GroupExaminationService service;
    private SecurityService securityService;
    private GroupExamination model;

    private Binder<GroupExamination> binder = new Binder<>(GroupExamination.class);
    private IntegerField id = new IntegerField("ID");
    private Select<User> teacher = new Select<>();
    private Select<StudentGroup> group = new Select<>();
    private Select<Test> test = new Select<>();
    private DateTimePicker openExamTime = new DateTimePicker("Начало экзамена");
    private DateTimePicker closeExamTime = new DateTimePicker("Окончание экзамена");

    private Button saveButton = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button cancelButton = new Button("Отмена");

    public GroupExaminationEditor(GroupExaminationService service,
                                  StudentGroupService groupService,
                                  TestService testService,
                                  SecurityService securityService) {
        this.service = service;
        this.securityService = securityService;

        id.setReadOnly(true);
        id.setWidthFull();
        add(id);

        teacher.setReadOnly(true);
        teacher.setWidthFull();
        teacher.setItemLabelGenerator(User::getName);
        add(teacher);

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
        add(formLayout);

        openExamTime.setWidthFull();
        openExamTime.setLocale(Locale.forLanguageTag("ru"));
        openExamTime.setMin(LocalDateTime.now());
        openExamTime.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                closeExamTime.setMin(LocalDateTime.now());
            } else {
                closeExamTime.setMin(event.getValue().plusHours(1));
            }
        });
        formLayout.add(openExamTime, 1);

        closeExamTime.setWidthFull();
        closeExamTime.setLocale(Locale.forLanguageTag("ru"));
        formLayout.add(closeExamTime, 1);


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

        service.startGroupExamination(new GroupExaminationRequest(
            model.getTest().getId(),
            model.getGroup().getId(),
            model.getTeacher().getId(),
            model.getOpenExamTime(),
            model.getCloseExamTime()
        ));
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

        if (model.getId() > 0) return;

        securityService.getAuthenticatedUser()
                .ifPresent(user -> {
                    teacher.setItems(user);
                    teacher.setValue(user);
                });
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

}

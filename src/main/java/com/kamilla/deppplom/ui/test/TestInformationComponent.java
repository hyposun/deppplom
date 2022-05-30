package com.kamilla.deppplom.ui.test;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.security.RolesAllowed;


@UIScope
@SpringComponent
@Route(value = "tests/:testId", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class TestInformationComponent extends VerticalLayout implements BeforeEnterObserver {

    private TestService testService;
    private Test test;
    private Binder<Test> binder = new Binder<>(Test.class);

    private TextField title = new TextField("Название");
    private TextField points = new TextField("Проходной балл");
    private Select<Discipline> discipline = new Select<>();
    private FormLayout formLayout = new FormLayout(title, points, discipline);

    public TestInformationComponent(TestService testService) {
        this.testService = testService;

        formLayout.setColspan(title, 3);
//        discipline.setReadOnly(true);
        discipline.setItemLabelGenerator(Discipline::getTitle);

        binder.bindInstanceFields(this);
        binder.forField(points)
                .withConverter(new StringToFloatConverter("Проходной балл должен быть числом"))
                .bind(Test::getMinimumPoints, Test::setMinimumPoints);

        add(formLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        test = findTest(event);
        if (test == null) {
            event.getUI().navigate(TestsView.class);
            return;
        }
        binder.setBean(test);
    }

    private Test findTest(BeforeEnterEvent event) {
        int testId = event.getRouteParameters().get("testId").map(NumberUtils::toInt).orElse(0);
        if (testId <= 0) return null;
        return testService.findById(testId).orElse(null);
    }


}

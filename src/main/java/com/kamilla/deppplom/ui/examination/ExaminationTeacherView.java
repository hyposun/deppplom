package com.kamilla.deppplom.ui.examination;

import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@Route(value = "test_admin", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
@PageTitle("Deppplom | Тесты")
public class ExaminationTeacherView {

    public ExaminationTeacherView() {

    }

}

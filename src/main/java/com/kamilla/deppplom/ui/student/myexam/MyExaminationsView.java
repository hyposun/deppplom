package com.kamilla.deppplom.ui.student.myexam;

import com.kamilla.deppplom.group_examination.GroupExamination;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Deppplom | Мои экзамены")
@Route(value = "my_examinations", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT"})
public class MyExaminationsView {

    private Grid<GroupExamination> examinationsGrid = new Grid<>();

}

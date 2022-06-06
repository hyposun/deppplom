package com.kamilla.deppplom.ui;

import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.discipline.DisciplineView;
import com.kamilla.deppplom.ui.examination.GroupExaminationView;
import com.kamilla.deppplom.ui.groups.GroupsVIew;
import com.kamilla.deppplom.ui.questions.QuestionsView;
import com.kamilla.deppplom.ui.test.TestsView;
import com.kamilla.deppplom.ui.users.UsersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

import javax.annotation.security.PermitAll;

@Route("/")
@PermitAll
public class BaseLayout extends AppLayout {

    private AccessAnnotationChecker accessChecker;
    private SecurityService service;

    public BaseLayout(AccessAnnotationChecker checker, SecurityService service) {
        this.accessChecker = checker;
        this.service = service;

        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Deppplom");
        title.getStyle()
             .set("font-size", "var(--lumo-font-size-l)")
             .set("margin", "0");

        Tabs tabs = getTabs();

        addToDrawer(tabs);
        addToNavbar(toggle, title);
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        addTab(tabs, VaadinIcon.USERS, "Пользователи", UsersView.class);
        addTab(tabs, VaadinIcon.GROUP, "Группы", GroupsVIew.class);
        addTab(tabs, VaadinIcon.AIRPLANE, "Дисциплины", DisciplineView.class);
        addTab(tabs, VaadinIcon.QUESTION, "Вопросы", QuestionsView.class);
        addTab(tabs, VaadinIcon.FOLDER, "Тесты", TestsView.class);
        addTab(tabs, VaadinIcon.CHART, "Экзамены", GroupExaminationView.class);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private void addTab(Tabs tabs, VaadinIcon viewIcon, String viewName, Class<? extends Component> viewClass) {

        if (!accessChecker.hasAccess(viewClass)) {
            return;
        }

        Icon icon = viewIcon.create();
        icon.getStyle()
            .set("box-sizing", "border-box")
            .set("margin-inline-end", "var(--lumo-space-m)")
            .set("margin-inline-start", "var(--lumo-space-xs)")
            .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        link.setRoute(viewClass);
        link.setTabIndex(-1);

        tabs.add(new Tab(link));
    }


}

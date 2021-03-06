package com.kamilla.deppplom.ui;

import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.admin.groups.GroupsVIew;
import com.kamilla.deppplom.ui.admin.users.UsersView;
import com.kamilla.deppplom.ui.common.ChatsView;
import com.kamilla.deppplom.ui.student.myexam.MyExaminationsView;
import com.kamilla.deppplom.ui.teacher.discipline.DisciplineView;
import com.kamilla.deppplom.ui.teacher.examination.GroupExaminationView;
import com.kamilla.deppplom.ui.teacher.questions.QuestionsView;
import com.kamilla.deppplom.ui.teacher.students.StudentsView;
import com.kamilla.deppplom.ui.teacher.test.TestsView;
import com.kamilla.deppplom.users.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
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

        var user = service.getAuthenticatedUser().orElseThrow();
        Button profile = new Button(user.getName(), e -> onProfileClick(user));
        profile.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout header = new HorizontalLayout(toggle, title, profile);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(title);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);
    }

    private void onProfileClick(User user) {
        getUI().ifPresent(ui -> {
            var params = new RouteParameters("userId", String.valueOf(user.getId()));
            ui.navigate(UserProfileView.class, params);
        });
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();

        // admin
        addTab(tabs, VaadinIcon.USERS, "????????????????????????", UsersView.class);
        addTab(tabs, VaadinIcon.GROUP, "????????????", GroupsVIew.class);

        // teacher
        addTab(tabs, VaadinIcon.AIRPLANE, "????????????????????", DisciplineView.class);
        addTab(tabs, VaadinIcon.QUESTION, "??????????????", QuestionsView.class);
        addTab(tabs, VaadinIcon.FOLDER, "??????????", TestsView.class);
        addTab(tabs, VaadinIcon.CHART, "????????????????", GroupExaminationView.class);
        addTab(tabs, VaadinIcon.USER_CLOCK, "????????????????", StudentsView.class);
        addTab(tabs, VaadinIcon.LAPTOP, "??????????????????", ChatsView.class);

        // students
        addTab(tabs, VaadinIcon.CHART, "?????? ????????????????", MyExaminationsView.class);

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

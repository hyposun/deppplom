package com.kamilla.deppplom.ui.admin.users;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import com.kamilla.deppplom.users.data.UserDataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@Route(value = "users", layout = BaseLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {

    private UserService service;
    private StudentGroupService studentGroupService;

    private Grid<User> grid = new Grid<>(User.class);
    private TextField nameFilter = new TextField("", "Имя");
    private Select<Role> roleFilter = new Select<>();
    private Select<StudentGroup> groupFilter = new Select<>();
    private Button reset = new Button("Сброс", VaadinIcon.CLOSE.create());
    private Button addNew = new Button("Добавить", VaadinIcon.PLUS.create());
    private MemoryBuffer memoryBuffer = new MemoryBuffer();
    private Upload upload = new Upload(memoryBuffer);
    private HorizontalLayout toolbar = new HorizontalLayout(nameFilter, roleFilter, groupFilter, reset, addNew, upload);

    private UserEditor userEditor;
    private UserDataService userDataService;

    @Autowired
    public UsersView(
            UserService service,
            StudentGroupService groupService,
            UserEditor userEditor,
            UserDataService userDataService
    ) {
        this.service = service;
        this.studentGroupService = groupService;
        this.userEditor = userEditor;
        this.userDataService = userDataService;
        add(toolbar, grid);
        toolbar.setAlignItems(Alignment.START);
        setupInteractions();
        showUsers();
        setupColumns();
    }

    private void setupInteractions() {

        roleFilter.setPlaceholder("Роль");
        roleFilter.setItemLabelGenerator(Role::getTitle);
        roleFilter.addValueChangeListener(event -> showUsers());
        roleFilter.setItems(Role.values());

        groupFilter.setPlaceholder("Группа");
        groupFilter.setItemLabelGenerator(StudentGroup::getTitle);
        groupFilter.addValueChangeListener(event -> showUsers());
        groupFilter.setItems(studentGroupService.findAll());

        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> showUsers());
        nameFilter.setPrefixComponent(VaadinIcon.SEARCH.create());

        grid.asSingleSelect().addValueChangeListener(event -> {
            this.userEditor.editUser(event.getValue());
        });

        userEditor.setChangeHandler(this::showUsers);

        addNew.addClickListener(event -> userEditor.editUser(new User()));

        reset.addClickListener(event -> {
            nameFilter.clear();
            roleFilter.clear();
            groupFilter.clear();
        });

        upload.setUploadButton(new Button("Импорт", VaadinIcon.FILE.create()));
        upload.setAcceptedFileTypes(".csv");
        upload.setMaxFiles(1);
        upload.setDropAllowed(false);
        upload.addSucceededListener(event -> importFromFile());

    }

    private void importFromFile() {
        try {
            List<User> users = userDataService.getUsersFromCsv(memoryBuffer.getInputStream());
            upload.clearFileList();
            showUsers();
            successNotification("Пользователей успешно загружено: " + users.size(), 2);
        } catch (Exception e) {
            upload.clearFileList();
            errorNotification("Не удалось загрузить пользователей из файла, проверьте корректность данных: " + e.getMessage(), 3);
        }
    }

    private void showUsers() {

        var groupId = groupFilter.isEmpty() ? null : groupFilter.getValue().getId();
        var role = roleFilter.isEmpty() ? null : roleFilter.getValue();
        var name = nameFilter.isEmpty() ? null : nameFilter.getValue();

        List<User> users = service.findAll(groupId, role, name);
        grid.setItems(users);
    }

    private void setupColumns() {

        grid.removeAllColumns();

        grid.addColumn(User::getId)
            .setHeader("ID")
            .setResizable(false)
            .setWidth("100px")
            .setFlexGrow(0);

        grid.addColumn(User::getName)
            .setHeader("ФИО");

        grid.addColumn(User::getLogin)
            .setHeader("Логин");

        grid.addColumn((user) -> user.getRole().getTitle())
            .setHeader("Роль");

        grid.addColumn((user) -> {
            return user.getGroups().stream()
                       .map(StudentGroup::getTitle)
                       .collect(Collectors.joining(", "));
        }).setHeader("Группы");

    }

}


package com.kamilla.deppplom.ui.groups;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupRepository;
import com.kamilla.deppplom.groups.data.StudentGroupDataService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@Route(value = "groups", layout = BaseLayout.class)
@RolesAllowed("ADMIN")
@PageTitle("Deppplom | Группы")
public class GroupsVIew extends VerticalLayout {

    private StudentGroupDataService studentGroupDataService;
    private StudentGroupRepository repository;
    private GroupEditor editor;

    private MemoryBuffer memoryBuffer = new MemoryBuffer();
    private Upload upload = new Upload(memoryBuffer);

    private Grid<StudentGroup> grid = new Grid<>(StudentGroup.class);
    private TextField filter = new TextField("", "Название");
    private Button addNew = new Button("Добавить", VaadinIcon.PLUS.create());
    private HorizontalLayout toolbar = new HorizontalLayout(filter, addNew, upload);

    public GroupsVIew(StudentGroupDataService studentGroupDataService, StudentGroupRepository repository, GroupEditor editor) {
        this.studentGroupDataService = studentGroupDataService;
        this.repository = repository;
        this.editor = editor;
        add(toolbar, grid);
        setupInteractivity();
        showGroups("");
        setHeightFull();
    }

    private void setupInteractivity() {

        upload.setUploadButton(new Button("Импорт", VaadinIcon.FILE.create()));
        upload.setAcceptedFileTypes(".csv");
        upload.setMaxFiles(1);
        upload.setDropAllowed(false);
        upload.addSucceededListener(event -> importFromFile());

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showGroups(event.getValue()));
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());

        grid.asSingleSelect()
            .addValueChangeListener(event -> editor.editGroup(event.getValue()));

        editor.setOnClose(() -> showGroups(filter.getValue()));

        addNew.addClickListener(event -> editor.editGroup(new StudentGroup()));

    }

    private void importFromFile() {
        try {
            List<StudentGroup> users = studentGroupDataService.getGroupsFromCsv(memoryBuffer.getInputStream());
            upload.clearFileList();
            showGroups("");
            successNotification("Групп успешно загружено: " + users.size(), 2);
        } catch (Exception e) {
            upload.clearFileList();
            errorNotification("Не удалось загрузить группы из файла, проверьте корректность данных: " + e.getMessage(), 3);
        }
    }

    private void showGroups(String name) {
        if (name == null || name.isBlank()) {
            grid.setItems(repository.findAll());
        } else {
            grid.setItems(repository.findAllByTitleLike("%" + name + "%"));
        }
        grid.removeAllColumns();
        grid.addColumn(StudentGroup::getId)
            .setHeader("ID")
            .setWidth("100px")
            .setFlexGrow(0)
            .setAutoWidth(false);
        grid.addColumn(StudentGroup::getTitle)
            .setHeader("Название");
    }

}


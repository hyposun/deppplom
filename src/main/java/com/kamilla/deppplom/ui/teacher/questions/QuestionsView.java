package com.kamilla.deppplom.ui.teacher.questions;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.question.model.QuestionType;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.teacher.questions.closed.ClosedQuestionView;
import com.kamilla.deppplom.ui.teacher.questions.opened.OpenQuestionView;
import com.kamilla.deppplom.ui.teacher.questions.ordered.OrderedClosedQuestionView;
import com.kamilla.deppplom.ui.utils.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("FieldMayBeFinal")
@Route(value = "questions", layout = BaseLayout.class)
@RolesAllowed({"TEACHER"})
@PageTitle("Deppplom | Вопросы")
public class QuestionsView extends VerticalLayout {

    private QuestionService questionService;
    private DisciplineService disciplineService;

    private Grid<QuestionInfo> grid = new Grid<>(QuestionInfo.class);
    private Select<Discipline> disciplineFilter = new Select<>();
    private Select<Difficulty> difficultyFilter = new Select<>();
    private Button reset = new Button("Сброс", VaadinIcon.CLOSE.create());

    private MenuBar addMenuBar = new MenuBar();
    private HorizontalLayout toolbar = new HorizontalLayout(disciplineFilter, difficultyFilter, reset, addMenuBar);

    public QuestionsView(
            QuestionService questionService,
            DisciplineService disciplineService
    ) {
        this.questionService = questionService;
        this.disciplineService = disciplineService;
        add(toolbar, grid);
        setHeightFull();

        setupGridColumns();
        setupFilters();
        setupEditors();
        showItems();
    }

    private void setupEditors() {

        grid.asSingleSelect()
            .addValueChangeListener(event -> {
                if (event.getValue() == null) return;
                var question = questionService.findQuestionById(event.getValue().id).get();
                openQuestionEditor(question.getType(), question.getId());
            });

        MenuItem item = addMenuBar.addItem("Добавить");
        SubMenu subMenu = item.getSubMenu();

        for (QuestionType value : QuestionType.values()) {
            subMenu.addItem(value.getTitle(), event -> openQuestionEditor(value, 0));
        }
    }

    private void openQuestionEditor(QuestionType type, int questionId) {

        Discipline discipline = disciplineFilter.getValue();
        if (discipline == null) {
            UIUtils.errorNotification("Сначала нужно выбрать дисциплину", 2);
            return;
        }

        Class<? extends Component> view;
        switch (type) {
            case CLOSED: view = ClosedQuestionView.class; break;
            case CLOSED_ORDERED: view = OrderedClosedQuestionView.class; break;
            case OPENED: view = OpenQuestionView.class; break;
            default: {
                UIUtils.errorNotification("Тип вопроса не поддерживается", 2);
                return;
            }
        }

        getUI().ifPresent(ui -> {
            RouteParam questionParam = new RouteParam("questionId", String.valueOf(questionId));
            RouteParam disciplineParam = new RouteParam("disciplineId", String.valueOf(discipline.getId()));
            RouteParameters parameters = new RouteParameters(questionParam, disciplineParam);
            ui.navigate(view, parameters);
        });
    }

    private void setupFilters() {

        List<Discipline> disciplines = disciplineService.findAll();
        disciplineFilter.setItems(disciplines);
        disciplines.stream().findFirst()
                .ifPresent(it -> disciplineFilter.setValue(it));
        disciplineFilter.setItemLabelGenerator(Discipline::getTitle);
        disciplineFilter.addValueChangeListener(event -> showItems());
        disciplineFilter.setPlaceholder("Дисциплина");

        difficultyFilter.setPlaceholder("Сложность");
        difficultyFilter.setItems(Difficulty.values());
        difficultyFilter.setItemLabelGenerator(Difficulty::getTitle);
        difficultyFilter.addValueChangeListener(event -> showItems());

        reset.addClickListener(event -> {
            disciplineFilter.clear();
            difficultyFilter.clear();
        });
    }

    private void showItems() {

        Discipline discipline = disciplineFilter.getValue();
        if (discipline == null) {
            grid.setItems(Collections.emptyList());
            return;
        }

        Stream<QuestionInfo> questions = questionService
                .findQuestionsByDisciplineId(discipline.getId()).stream()
                .map(question -> new QuestionInfo(question.getId(), question.getTitle(), question.getDifficulty(), question.getType()));

        if (!difficultyFilter.isEmpty()) {
            Difficulty expected = difficultyFilter.getValue();
            questions = questions.filter(it -> it.getDifficulty() == expected);
        }

        grid.setItems(questions.collect(Collectors.toList()));
    }

    private void setupGridColumns() {
        grid.removeAllColumns();
        grid.addColumn(QuestionInfo::getId)
            .setHeader("ID")
            .setAutoWidth(true);
        grid.addColumn(QuestionInfo::getTitle)
            .setHeader("Название")
            .setAutoWidth(true);
        grid.addColumn(info -> info.getDifficulty().getTitle())
            .setHeader("Сложность")
            .setAutoWidth(true);
        grid.addColumn(it -> it.getType().getTitle())
            .setHeader("Тип вопроса")
            .setAutoWidth(true);
    }

    @AllArgsConstructor
    @Data
    private static class QuestionInfo {

        protected int id;

        protected String title;

        protected Difficulty difficulty;

        protected QuestionType type;

    }

}

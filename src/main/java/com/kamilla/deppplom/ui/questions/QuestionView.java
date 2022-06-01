package com.kamilla.deppplom.ui.questions;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.question.model.QuestionType;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.questions.closed.ClosedQuestionView;
import com.kamilla.deppplom.ui.questions.ordered.OrderedCloseQuestionEditor;
import com.kamilla.deppplom.ui.utils.UIUtils;
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
@RolesAllowed({"ADMIN", "TEACHER"})
@PageTitle("Deppplom | Вопросы")
public class QuestionView extends VerticalLayout {

    private QuestionService questionService;
    private DisciplineService disciplineService;
    private OrderedCloseQuestionEditor orderedCloseQuestionEditor;

    private Grid<QuestionInfo> grid = new Grid<>(QuestionInfo.class);
    private Select<Discipline> disciplineFilter = new Select<>();
    private Select<Difficulty> difficultyFilter = new Select<>();
    private Button reset = new Button("Сброс", VaadinIcon.CLOSE.create());

    private MenuBar addMenuBar = new MenuBar();
    private HorizontalLayout toolbar = new HorizontalLayout(disciplineFilter, difficultyFilter, reset, addMenuBar);

    public QuestionView(
            QuestionService questionService,
            DisciplineService disciplineService,
            OrderedCloseQuestionEditor orderedCloseQuestionEditor) {
        this.questionService = questionService;
        this.disciplineService = disciplineService;
        this.orderedCloseQuestionEditor = orderedCloseQuestionEditor;
        add(toolbar, grid);
        setHeightFull();

        setupGridColumns();
        setupFilters();
        setupEditors();
        showItems();
    }

    private void setupEditors() {

        orderedCloseQuestionEditor.setOnClose(this::showItems);

        grid.asSingleSelect()
            .addValueChangeListener(event -> {
                if (event.getValue() == null) return;
                var question = questionService.findQuestionById(event.getValue().id).get();
                if (question.getType() == QuestionType.CLOSED) {
                    openClosedQuestion(question.getId(), question.getDisciplineId());
                }
                if (question.getType() == QuestionType.CLOSED_ORDERED) {
                    orderedCloseQuestionEditor.editQuestion((OrderedClosedQuestion) question);
                }
            });

        MenuItem item = addMenuBar.addItem("Добавить");
        SubMenu subMenu = item.getSubMenu();

        for (QuestionType value : QuestionType.values()) {
            subMenu.addItem(value.getTitle(), event -> openEditor(value));
        }
    }

    private void openEditor(QuestionType value) {

        orderedCloseQuestionEditor.setOnClose(this::showItems);

        Discipline discipline = disciplineFilter.getValue();
        if (discipline == null) {
            UIUtils.errorNotification("Сначала нужно выбрать дисциплину", 2);
            return;
        }

        if (value == QuestionType.CLOSED) {
            openClosedQuestion(0, discipline.getId());
        } else if (value == QuestionType.CLOSED_ORDERED) {
            OrderedClosedQuestion question = new OrderedClosedQuestion();
            question.setDisciplineId(discipline.getId());
            orderedCloseQuestionEditor.editQuestion(question);
        }
    }

    private void openClosedQuestion(int questionId, int disciplineId) {
        getUI().ifPresent(ui -> {
            RouteParam questionParam = new RouteParam("questionId", String.valueOf(questionId));
            RouteParam disciplineParam = new RouteParam("disciplineId", String.valueOf(disciplineId));
            RouteParameters parameters = new RouteParameters(questionParam, disciplineParam);
            ui.navigate(ClosedQuestionView.class, parameters);
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

package com.kamilla.deppplom.ui.questions;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.question.model.QuestionType;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.UIUtils;
import com.kamilla.deppplom.ui.questions.closed.ClosedQuestionEditor;
import com.kamilla.deppplom.ui.questions.ordered.OrderedCloseQuestionEditor;
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
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("FieldMayBeFinal")
@Route(value = "questions", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
@PageTitle("Deppplom | Вопросы")
public class QuestionView extends VerticalLayout {

    private QuestionService questionService;
    private DisciplineService disciplineService;
    private ClosedQuestionEditor closedQuestionEditor;
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
            ClosedQuestionEditor closedQuestionEditor,
            OrderedCloseQuestionEditor orderedCloseQuestionEditor) {
        this.questionService = questionService;
        this.disciplineService = disciplineService;
        this.closedQuestionEditor = closedQuestionEditor;
        this.orderedCloseQuestionEditor = orderedCloseQuestionEditor;
        add(toolbar, grid);
        setHeightFull();

        setupGridColumns();
        setupFilters();
        setupEditors();
        showItems();
    }

    private void setupEditors() {

        closedQuestionEditor.setOnClose(this::showItems);
        orderedCloseQuestionEditor.setOnClose(this::showItems);

        grid.asSingleSelect()
            .addValueChangeListener(event -> {
                if (event.getValue() == null) return;
                var question = questionService.findQuestionById(event.getValue().id).get();
                if (question.getType() == QuestionType.CLOSED) {
                    closedQuestionEditor.editQuestion((ClosedQuestion) question);
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

        disciplineFilter.setPlaceholder("Дисциплина");
        disciplineFilter.addValueChangeListener(event -> showItems());
    }

    private void openEditor(QuestionType value) {

        closedQuestionEditor.setOnClose(this::showItems);
        orderedCloseQuestionEditor.setOnClose(this::showItems);

        Discipline discipline = disciplineFilter.getValue();
        if (discipline == null) {
            UIUtils.errorNotification("Сначала выберите дисциплину", 2);
            return;
        }

        if (value == QuestionType.CLOSED) {
            ClosedQuestion question = new ClosedQuestion();
            question.setDisciplineId(discipline.getId());
            closedQuestionEditor.editQuestion(question);
        } else if (value == QuestionType.CLOSED_ORDERED) {
            OrderedClosedQuestion question = new OrderedClosedQuestion();
            question.setDisciplineId(discipline.getId());
            orderedCloseQuestionEditor.editQuestion(question);
        }
    }

    private void setupFilters() {
        disciplineFilter.setItems(disciplineService.findAll());
        disciplineFilter.setItemLabelGenerator(Discipline::getTitle);
        disciplineFilter.addValueChangeListener(event -> showItems());

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
                .setWidth("100px")
                .setFlexGrow(0)
                .setAutoWidth(false);
        grid.addColumn(QuestionInfo::getTitle)
            .setHeader("Название");
        grid.addColumn(info -> info.getDifficulty().getTitle())
            .setHeader("Сложность")
            .setWidth("100px")
            .setAutoWidth(false);
        grid.addColumn(it -> it.getType().getTitle())
            .setHeader("Тип вопроса");
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

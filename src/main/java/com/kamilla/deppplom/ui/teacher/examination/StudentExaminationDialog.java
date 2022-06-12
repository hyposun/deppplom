package com.kamilla.deppplom.ui.teacher.examination;

import com.kamilla.deppplom.examination.model.QuestionExamination;
import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.report.model.StudentExaminationReport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.Collections;
import java.util.List;

@UIScope
@SpringComponent
public class StudentExaminationDialog extends Dialog {

    private H3 title = new H3();
    private Grid<QuestionExamination> questionsGrid = new Grid<>();

    public StudentExaminationDialog() {

        questionsGrid
                .addColumn(it -> it.getQuestion().getTitle())
                .setHeader("Вопрос").setAutoWidth(true);
        questionsGrid
                .addColumn(it -> it.getQuestion().getDifficulty().getTitle())
                .setHeader("Сложность").setAutoWidth(true);
        questionsGrid
                .addColumn(QuestionExamination::getAnswer)
                .setHeader("Ответ").setAutoWidth(true);
        questionsGrid
                .addColumn(it -> it.getQuestion().getResultDescription())
                .setHeader("Пояснение").setAutoWidth(true);
        questionsGrid
                .addColumn(QuestionExamination::getPoints)
                .setHeader("Баллы").setAutoWidth(true);

        setVisible(false);

        setMaxHeight("35em");
        setWidthFull();

        add(title);
        add(questionsGrid);

    }

    public void show(StudentExaminationReport report) {
        StudentExamination studentExam = report.getStudentExamination();
        List<QuestionExamination> items = studentExam != null ? studentExam.getResultList() : Collections.emptyList();
        questionsGrid.setItems(items);
        title.setText(report.getStudent().getName());
        setVisible(true);
        open();
    }

}

package com.kamilla.deppplom.ui.teacher.test;

import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.export.TextPdfExport;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.tests.model.TestVersion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

@UIScope
@SpringComponent
public class TestVersionsEditor extends VerticalLayout {

    private TextPdfExport textPdfExport;
    private TestService service;
    private Test test;

    private Button generateSelectiveButton = new Button("Создать вручную");
    private Button generateRandomButton = new Button("Создать автоматически");
    private Grid<Version> versionsGrid = new Grid<>();
    private VersionQuestionsDialog questionsDialog;

    public TestVersionsEditor(
            TestService service,
            RandomTestVersionGenerationDialog randomEditDialog,
            SelectiveTestVersionGenerationDialog selectiveEditDialog,
            TextPdfExport textPdfExport,
            VersionQuestionsDialog questionsDialog
    ) {
        this.service = service;
        this.textPdfExport = textPdfExport;
        this.questionsDialog = questionsDialog;

        add(new HorizontalLayout(generateSelectiveButton, generateRandomButton));
        add(new H3("Версии"));
        add(versionsGrid);
        add(questionsDialog);

        versionsGrid
                .addColumn(Version::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        versionsGrid
                .addColumn(Version::getDescription)
                .setHeader("Описание")
                .setAutoWidth(true);

        versionsGrid
                .addComponentColumn(this::getDownloadVersionPdfLink)
                .setHeader("PDF")
                .setAutoWidth(true);

        versionsGrid.asSingleSelect()
                        .addValueChangeListener(item -> {
                            if (item.getValue() == null) return;
                            showVersionQuestions(item.getValue());
                        });

        add(randomEditDialog);
        add(selectiveEditDialog);

        generateRandomButton.addClickListener(event -> {
            randomEditDialog.show(test.getId(), () -> show(test.getId()));
        });
        generateSelectiveButton.addClickListener(event -> {
            selectiveEditDialog.show(test, () -> show(test.getId()));
        });

    }

    @SneakyThrows
    private DownloadVersionPdfLink getDownloadVersionPdfLink(Version it) {
        var testVersion = test.getVersions().stream()
                .filter(version -> version.getId() == it.id)
                .findAny().orElseThrow();
        return new DownloadVersionPdfLink(textPdfExport, test, testVersion);
    }

    private void showVersionQuestions(Version version) {
        test.getVersions().stream()
            .filter(it -> it.getId() == version.getId())
            .findFirst()
            .ifPresent(questionsDialog::show);
    }

    public void show(int testId) {
        service.findById(testId)
                .ifPresent(it -> {
                    test = it;
                    init();
                });
    }

    private void init() {
        List<Version> versions = test.getVersions().stream()
                .map(this::toVersion)
                .collect(Collectors.toList());
        versionsGrid.setItems(versions);
        setVisible(true);
    }

    private Version toVersion(TestVersion testVersion) {
        Version version = new Version();
        version.setId(testVersion.getId());
        var description = testVersion.getQuestions().stream()
                .collect(groupingBy(Question::getDifficulty))
                .entrySet().stream()
                .map(it -> it.getKey().getTitle() + " = " + it.getValue().size())
                .collect(joining(", "));
        version.setDescription(description);
        return version;
    }

    @Data
    private static class Version {
        private int id;
        private String description;
    }
}

package com.kamilla.deppplom.ui.test;

import com.kamilla.deppplom.tests.model.TestVersion;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@UIScope
@SpringComponent
public class VersionQuestionsDialog extends Dialog {

    private Grid<Item> questionsGrid = new Grid<>();

    public VersionQuestionsDialog() {
        add(questionsGrid);
        questionsGrid.setWidthFull();
        questionsGrid.addColumn(Item::getId).setHeader("ID");
        questionsGrid.addColumn(Item::getTitle).setHeader("Название");
        questionsGrid.addColumn(Item::getDifficulty).setHeader("Сложность");
        questionsGrid.addColumn(Item::getType).setHeader("Тип");
        setWidthFull();
        setVisible(false);
    }

    public void show(TestVersion version) {
        List<Item> items = version.getQuestions().stream()
                .map(it -> new Item(it.getId(), it.getTitle(), it.getDifficulty().getTitle(), it.getType().getTitle()))
                .collect(Collectors.toList());
        questionsGrid.setItems(items);
        open();
        setVisible(true);
    }

    @Data
    @AllArgsConstructor
    private static class Item {
        int id;
        String title;
        String difficulty;
        String type;
    }

}

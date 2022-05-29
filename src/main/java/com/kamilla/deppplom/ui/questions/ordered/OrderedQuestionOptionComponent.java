package com.kamilla.deppplom.ui.questions.ordered;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

public class OrderedQuestionOptionComponent extends HorizontalLayout {

    private TextField id = new TextField("ID");
    private TextField title = new TextField("Ответ");
    private Button remove = new Button("Удалить");

    @Setter
    private Runnable onDelete;

    public OrderedQuestionOptionComponent(
        int id,
        String title
    ) {
        this.id.setValue(String.valueOf(id));
        this.id.setReadOnly(true);
        this.id.setWidth("50px");

        if (title != null) this.title.setValue(title);

        add(this.id, this.title, remove);

        setVerticalComponentAlignment(Alignment.CENTER);
        setAlignItems(Alignment.BASELINE);

        remove.addClickListener(event -> onDelete.run());
    }

    public Option getOption() {
        return new Option(Integer.parseInt(id.getValue()), title.getValue());
    }

    @Data
    @AllArgsConstructor
    public static class Option {

        private int id;
        private String title;

        public boolean isEmpty() {
            return title == null || title.isBlank();
        }

    }


}

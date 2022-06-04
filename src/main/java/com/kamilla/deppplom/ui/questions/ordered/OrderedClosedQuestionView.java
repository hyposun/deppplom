package com.kamilla.deppplom.ui.questions.ordered;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.questions.BaseQuestionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static java.util.Arrays.stream;

@SpringComponent
@UIScope
@Route(value = "questions/:disciplineId/:questionId/ordered", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class OrderedClosedQuestionView extends BaseQuestionView<OrderedClosedQuestion> {

    protected TextField validOrderIds;
    protected Button newOptionButton;
    protected VerticalLayout optionsLayout;
    protected List<OrderedQuestionOptionComponent> optionComponents = new ArrayList<>();

    public OrderedClosedQuestionView(QuestionService questionService) {
        super(questionService);
    }

    @Override
    protected Class<OrderedClosedQuestion> getQuestionType() {
        return OrderedClosedQuestion.class;
    }

    @Override
    protected OrderedClosedQuestion buildNewQuestion() {
        return new OrderedClosedQuestion();
    }

    @Override
    protected List<Component> getAdditionComponents() {
        validOrderIds = new TextField("Номера ответов в правильном порядке");
        newOptionButton = new Button("Добавить ответ", VaadinIcon.PLUS.create());
        newOptionButton.addClickListener(event ->
            addOption(optionComponents.size() + 1, "")
        );
        optionsLayout = new VerticalLayout(newOptionButton);
        setFullWidth(validOrderIds);
        return Arrays.asList(optionsLayout, new VerticalLayout(validOrderIds));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);
        question.getOptions()
                .forEach(it -> addOption(it.getId(), it.getTitle()));
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        super.beforeLeave(event);
        optionComponents.forEach(it -> optionsLayout.remove(it));
        optionComponents.clear();
    }

    @Override
    protected void save() {

        var possibleAnswers = optionComponents.stream()
                .map(OrderedQuestionOptionComponent::getOption)
                .collect(Collectors.toList());

        for (OrderedQuestionOptionComponent.Option answer : possibleAnswers) {
            if (answer.isEmpty()) {
                errorNotification("Заполните все ответы, либо удалите лишние", 3);
                return;
            }
        }

        String value = validOrderIds.getValue();
        if (value.isEmpty()) {
            errorNotification("Укажите корректный правильный порядок ответов", 2);
            return;
        }

        List<OrderedClosedQuestion.Option> options = possibleAnswers.stream()
                .map(option -> new OrderedClosedQuestion.Option(option.getId(), option.getTitle()))
                .collect(Collectors.toList());

        List<Integer> validOrderedOptions = stream(value.split(",")).sequential()
                .map(it -> Integer.parseInt(it.trim()))
                .collect(Collectors.toList());

        boolean illegalIds = !validOrderedOptions.stream()
                .allMatch(id -> options.stream()
                        .anyMatch(option -> option.getId() == id));
        if (illegalIds) {
            errorNotification("В правиальном ответе указан ID несуществующего вариант", 3);
            return;
        }

        question.setOptions(options);
        question.setValidOrderedOptions(validOrderedOptions);

        super.save();
    }

    private void addOption(int id, String title) {
        OrderedQuestionOptionComponent component = new OrderedQuestionOptionComponent(id, title);
        optionsLayout.add(component);
        optionComponents.add(component);
        component.setOnDelete(() -> {
            optionsLayout.remove(component);
            optionComponents.remove(component);
        });
    }

}

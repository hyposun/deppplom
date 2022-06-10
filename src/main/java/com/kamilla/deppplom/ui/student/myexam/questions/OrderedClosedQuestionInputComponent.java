package com.kamilla.deppplom.ui.student.myexam.questions;

import com.kamilla.deppplom.examination.StudentExaminationService;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderClosedQuestionSelection;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion.Option;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.student.myexam.service.MyExaminationDispatcher;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@SpringComponent
@UIScope
@Route(value = "my_examination/:examinationId/ordered_closed_questions/:questionId", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT"})
public class OrderedClosedQuestionInputComponent extends BaseQuestionInputComponent<OrderedClosedQuestion> {

    private VerticalLayout variants = new VerticalLayout();
    private TextField answer = new TextField("Ответ");

    public OrderedClosedQuestionInputComponent(MyExaminationDispatcher dispatcher, QuestionService questionService, StudentExaminationService studentExaminationService) {
        super(dispatcher, questionService, studentExaminationService);
        questionBodyLayout.add(variants, answer);
        answer.setLabel("Укажите через запятую номера ответов в правильном порядке");
    }

    @Override
    protected void setupState() {
        super.setupState();
        variants.removeAll();
        for (Option option : question.getOptions()) {
            Span span = new Span(option.getId() + ". " + option.getTitle());
            variants.add(span);
        }
    }

    @Override
    protected Selection getSelectionOrNull() {
        String value = answer.getValue();
        if (isBlank(value)) return null;

        List<Integer> selectedItems = Arrays.stream(value.split(","))
                .map(it -> NumberUtils.toInt(it.trim()))
                .filter(it -> it > 0)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) return null;

        var selection = new OrderClosedQuestionSelection();
        selection.setSelectedOptions(selectedItems);
        return selection;
    }
}

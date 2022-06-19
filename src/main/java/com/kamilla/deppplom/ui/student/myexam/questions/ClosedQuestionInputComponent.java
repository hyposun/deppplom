package com.kamilla.deppplom.ui.student.myexam.questions;

import com.kamilla.deppplom.examination.StudentExaminationService;
import com.kamilla.deppplom.media.MediaService;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion.Option;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestionSelection;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.student.myexam.service.MyExaminationDispatcher;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import javax.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
@Route(value = "my_examination/:examinationId/closed_questions/:questionId", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT"})
public class ClosedQuestionInputComponent extends BaseQuestionInputComponent<ClosedQuestion> {

    private MediaService mediaService;
    private VerticalLayout optionsLayout = new VerticalLayout();
    private List<OptionComponent> options = new ArrayList<>();

    public ClosedQuestionInputComponent(
            MyExaminationDispatcher dispatcher,
            QuestionService questionService,
            StudentExaminationService studentExaminationService,
            MediaService mediaService
    ) {
        super(dispatcher, questionService, studentExaminationService);
        this.mediaService = mediaService;
        questionBodyLayout.add(optionsLayout);
    }

    @Override
    protected void setupState() {
        super.setupState();

        question.getOptions().forEach(option -> {
            OptionComponent component = new OptionComponent(mediaService, option);
            optionsLayout.add(component);
            options.add(component);
        });
    }

    @Override
    protected Selection getSelectionOrNull() {
        var selected = options.stream()
                .filter(OptionComponent::isSelected)
                .map(it -> it.option.getId())
                .collect(Collectors.toSet());

        if (selected.isEmpty()) return null;

        return new ClosedQuestionSelection(selected);
    }

    private static class OptionComponent extends VerticalLayout {

        Option option;

        private Checkbox checkbox = new Checkbox(false);

        public OptionComponent(MediaService mediaService, Option option) {

            this.option = option;

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(checkbox);
            horizontalLayout.add(new Span(option.getTitle()));
            add(horizontalLayout);

            Optional.ofNullable(option.getImageMediaId())
                    .flatMap(mediaService::findById)
                    .ifPresent(media -> {
                        Image image = new Image();
                        image.setMaxWidth("300px");
                        image.setSrc(new StreamResource(media.getName(), () -> {
                            byte[] content = mediaService.download(media.getId());
                            return new ByteArrayInputStream(content);
                        }));
                        add(image);
                    });
        }

        public boolean isSelected() {
            return checkbox.getValue();
        }
    }

}

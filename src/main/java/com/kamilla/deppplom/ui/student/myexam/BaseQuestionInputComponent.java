package com.kamilla.deppplom.ui.student.myexam;

import com.kamilla.deppplom.examination.StudentExaminationService;
import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.ui.student.myexam.service.MyExaminationDispatcher;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouteParameters;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class BaseQuestionInputComponent<Q extends Question> extends VerticalLayout implements BeforeEnterObserver {

    protected MyExaminationDispatcher dispatcher;
    protected QuestionService questionService;
    protected StudentExaminationService studentExaminationService;
    protected StudentExamination examination;
    protected Q question;

    protected ProgressBar progressBar = new ProgressBar();
    protected H2 titleField = new H2();
    protected Span descriptionArea = new Span();

    protected VerticalLayout questionBodyLayout = new VerticalLayout();

    H3 resultHeader = new H3();
    protected Span explanation = new Span();
    protected VerticalLayout resultLayout = new VerticalLayout(resultHeader, explanation);

    protected Button submitButton = new Button("Отправить", VaadinIcon.CHECK.create());
    protected Button nextButton = new Button("Следующий вопрос");
    protected HorizontalLayout controlLayout = new HorizontalLayout(submitButton, nextButton);

    public BaseQuestionInputComponent(
        MyExaminationDispatcher dispatcher,
        QuestionService questionService,
        StudentExaminationService studentExaminationService
    ) {
        super();
        this.dispatcher = dispatcher;
        this.questionService = questionService;
        this.studentExaminationService = studentExaminationService;

        progressBar.setWidthFull();
        progressBar.setMin(0);
        progressBar.setMax(1);
        add(progressBar);

        titleField.setWidthFull();
        add(titleField);

        descriptionArea.setWidthFull();
        add(descriptionArea);

        questionBodyLayout.setMargin(false);
        questionBodyLayout.setPadding(false);
        add(questionBodyLayout);

        explanation.setWidthFull();
        resultLayout.setPadding(false);
        resultLayout.setMargin(false);
        add(resultLayout);

        controlLayout.setMargin(false);
        controlLayout.setPadding(false);
        add(controlLayout);

        submitButton.addClickListener(event -> onSubmit());
        nextButton.addClickListener(event -> {
            getUI().ifPresent(ui -> dispatcher.dispatch(examination.getGroupExaminationId(), ui));
        });

        setHeightFull();
    }

    private void onSubmit() {

        var selection = getSelectionOrNull();
        if (selection == null) {
            errorNotification("Заполните все необходимые поля", 2);
            return;
        }

        CheckResult result = question.check(selection);

        resultHeader.setText("Результат: " +  result.getPoints() + " баллов");
        explanation.setText(result.getMessage());
        resultLayout.setVisible(true);

        submitButton.setVisible(false);
        nextButton.setVisible(true);

        studentExaminationService.addAnswer(examination.getId(), question.getId(), selection);
    }

    abstract protected Selection getSelectionOrNull();

    @SuppressWarnings("unchecked")
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters params = event.getRouteParameters();
        examination = params.get("examinationId")
                .map(Integer::parseInt)
                .map(it -> studentExaminationService.findById(it)).orElseThrow();
        var questionId = params.get("questionId").map(Integer::parseInt).orElseThrow();
        question = (Q) questionService.findQuestionById(questionId).orElseThrow();
        setupState();
    }

    protected void setupState() {

        int all = examination.getTestVersion().getQuestions().size();
        int completed = examination.getResultList().size();

        double progress = (double) all / (double) 100 * (double) completed;
        progressBar.setValue(progress);

        titleField.setText(question.getTitle());

        if (!isBlank(question.getDescription())) {
            descriptionArea.setText(question.getDescription());
            descriptionArea.setVisible(true);
        } else {
            descriptionArea.setVisible(false);
        }

        submitButton.setVisible(true);
        nextButton.setVisible(false);

        resultLayout.setVisible(false);
    }

}
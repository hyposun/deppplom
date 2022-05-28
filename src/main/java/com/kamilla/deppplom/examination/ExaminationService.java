package com.kamilla.deppplom.examination;

import com.kamilla.deppplom.examination.model.Examination;
import com.kamilla.deppplom.examination.model.QuestionExamination;
import com.kamilla.deppplom.examination.repository.ExaminationEntity;
import com.kamilla.deppplom.examination.repository.ExaminationRepository;
import com.kamilla.deppplom.examination.repository.QuestionExaminationEntity;
import com.kamilla.deppplom.examination.repository.QuestionExaminationRepository;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.tests.model.TestVersion;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ExaminationService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    TestService testService;

    @Autowired
    ExaminationRepository repository;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionExaminationRepository questionExaminationRepository;

    public Examination startExamination(int userId, int testId) {

         var user = getUser(userId);
         var test = getTest(testId);

         var resultEntity = new ExaminationEntity();
         resultEntity.setStudentId(userId);
         resultEntity.setTestId(testId);
         resultEntity.setStarted(ZonedDateTime.now());
         resultEntity.setPoints(0.0F);
         resultEntity.setTestVersionId(getRandomVersion(test));

         resultEntity = repository.save(resultEntity);

         return fromEntity(resultEntity,user,test);
    }

    public Examination findById(int examintationId) {
        return repository.findById(examintationId)
                .map(this::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Прохождение теста не найдено"));
    }

    public Optional<Question<Selection>> getNextQuestion(int examinationId) {

        ExaminationEntity examination = getExamination(examinationId);
        Test test = getTest(examination.getTestId());

        TestVersion version = test.getVersions().stream()
                .filter(it -> it.getId() == examination.getTestVersionId()).findFirst()
                .get();

        List<Question<Selection>> questions = version.getQuestions();
        List<QuestionExaminationEntity> resultList = examination.getResultList();

        List<Question<Selection>> nonAnsweredQuestions = questions.stream()
                .filter(question -> {
                    var processedQuestion = resultList.stream().filter(it -> it.getQuestionId() == question.getId()).findAny();
                    return processedQuestion.isEmpty();
                }).collect(Collectors.toList());

        Optional<Question<Selection>> nextQuestion = nonAnsweredQuestions.stream().findFirst();
        if (nextQuestion.isEmpty()) {
            finalizeExamination(examination, test);
        }
        return nextQuestion;
    }

    public void addAnswer(int examinationId, int questionId, Selection selection) {

        ExaminationEntity examinationEntity = getExamination(examinationId);
        var question = getQuestion(questionId);

        CheckResult checkResult = question.check(selection);
        QuestionExaminationEntity resultEntity = new QuestionExaminationEntity();
        resultEntity.setResultPoints(checkResult.getPoints());
        resultEntity.setAnswer(selection.getString());
        resultEntity.setQuestionId(questionId);

        resultEntity = questionExaminationRepository.save(resultEntity);

        examinationEntity.getResultList().add(resultEntity);
        repository.save(examinationEntity);
    }

    private User getUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    private void finalizeExamination(ExaminationEntity examination, Test test) {

        float totalPoints = examination.getResultList().stream()
                .map(QuestionExaminationEntity::getResultPoints)
                .reduce(0f, Float::sum);

        var success = totalPoints >= test.getMinimumPoints();

        examination.setSuccess(success);
        examination.setPoints(totalPoints);
        examination.setFinished(ZonedDateTime.now());

        repository.save(examination);
    }

    private ExaminationEntity getExamination(int examinationId) {
        return repository.findById(examinationId).orElseThrow(() -> new IllegalArgumentException("Прохождение теста не зарегистрировано"));
    }

    private Test getTest(int testId) {
        return testService.findById(testId).orElseThrow(() -> new IllegalArgumentException("Тест не найден"));
    }

    private int getRandomVersion(Test test) {
        List<TestVersion> versions = test.getVersions();
        Random random = new Random();
        int index = random.nextInt(versions.size());
        return versions.get(index).getId();
    }

    private Question<Selection> getQuestion(int questionId) {
        return questionService.findQuestionById(questionId).orElseThrow(() -> new IllegalArgumentException("Вопрос не найден"));
    }

    private Examination fromEntity(ExaminationEntity entity) {
        var test = getTest(entity.getTestId());
        var user = getUser(entity.getStudentId());
        return fromEntity(entity, user, test);
    }

    private Examination fromEntity(ExaminationEntity entity, User student, Test test) {

        List<QuestionExamination> resultList = entity.getResultList().stream()
                .map(this::getQuestionResult)
                .collect(Collectors.toList());

         var result = new Examination();
         result.setStarted(entity.getStarted());
         result.setStudent(student);
         result.setTest(test);
         result.setFinished(entity.getFinished());
         result.setPoints(entity.getPoints());
         result.setResultList(resultList);

        return result;
    }

    private QuestionExamination getQuestionResult(QuestionExaminationEntity it) {
        Question<Selection> question = getQuestion(it.getQuestionId());
        QuestionExamination questionExamination = new QuestionExamination();
        questionExamination.setId(it.getId());
        questionExamination.setQuestion(question);
        questionExamination.setPoints(it.getResultPoints());
        questionExamination.setAnswer(it.getAnswer());
        return questionExamination;
    }


}


package com.kamilla.deppplom.examination;

import com.kamilla.deppplom.examination.model.QuestionExamination;
import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.examination.repository.ExaminationEntity;
import com.kamilla.deppplom.examination.repository.ExaminationRepository;
import com.kamilla.deppplom.examination.repository.QuestionExaminationEntity;
import com.kamilla.deppplom.examination.repository.QuestionExaminationRepository;
import com.kamilla.deppplom.group_examination.service.GroupExaminationService;
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
public class StudentExaminationService {


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

    @Autowired
    GroupExaminationService groupExaminationService;

    public StudentExamination startExamination(int userId, int groupExaminationId) {

        Optional<StudentExamination> existing = findByStudentIdAndGroupExaminationId(userId, groupExaminationId);
        if (existing.isPresent()) return existing.get();

        var user = getUser(userId);
        var groupExam = groupExaminationService.findById(groupExaminationId).orElseThrow();
        var test = groupExam.getTest();

        TestVersion version = getRandomVersion(test);

        var resultEntity = new ExaminationEntity();
        resultEntity.setStudentId(userId);
        resultEntity.setTestId(test.getId());
        resultEntity.setStarted(ZonedDateTime.now());
        resultEntity.setPoints(0.0F);
        resultEntity.setTestVersionId(version.getId());
        resultEntity.setGroupExaminationId(groupExaminationId);

        resultEntity = repository.save(resultEntity);

        return fromEntity(resultEntity, user, test);
    }

    public Optional<StudentExamination> findByStudentIdAndGroupExaminationId(int studentId, int groupExaminationId) {
        return repository
                .findByStudentIdAndGroupExaminationId(studentId, groupExaminationId).stream()
                .map(this::fromEntity)
                .findAny();
    }

    public StudentExamination findById(int examinationId) {
        return repository.findById(examinationId)
                .map(this::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Прохождение теста не найдено"));
    }

    public Optional<Question> getNextQuestion(int examinationId) {

        ExaminationEntity examination = getExamination(examinationId);
        Test test = getTest(examination.getTestId());

        TestVersion version = test.getVersions().stream()
                .filter(it -> it.getId() == examination.getTestVersionId()).findFirst()
                .get();

        List<Question> questions = version.getQuestions();
        List<QuestionExaminationEntity> resultList = examination.getResultList();

        List<Question> nonAnsweredQuestions = questions.stream()
                .filter(question -> {
                    var processedQuestion = resultList.stream().filter(it -> it.getQuestionId() == question.getId()).findAny();
                    return processedQuestion.isEmpty();
                }).collect(Collectors.toList());

        Optional<Question> nextQuestion = nonAnsweredQuestions.stream().findFirst();
        if (nextQuestion.isEmpty()) {
            finalizeExamination(examination, test);
        }
        return nextQuestion;
    }

    public void addAnswer(int examinationId, int questionId, Selection selection) {

        ExaminationEntity examinationEntity = getExamination(examinationId);
        var question = questionService.getQuestionById(questionId);

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

    private TestVersion getRandomVersion(Test test) {
        List<TestVersion> versions = test.getVersions();
        Random random = new Random();
        int index = random.nextInt(versions.size());
        return versions.get(index);
    }

    private StudentExamination fromEntity(ExaminationEntity entity) {
        var test = getTest(entity.getTestId());
        var user = getUser(entity.getStudentId());
        return fromEntity(entity, user, test);
    }

    private StudentExamination fromEntity(ExaminationEntity entity, User student, Test test) {

        List<QuestionExamination> resultList = entity.getResultList().stream()
                .map(this::getQuestionResult)
                .collect(Collectors.toList());

        TestVersion version = test.getVersions().stream()
                .filter(it -> it.getId() == entity.getTestVersionId())
                .findAny().orElseThrow();

        var result = new StudentExamination();
        result.setId(entity.getId());
        result.setStarted(entity.getStarted());
        result.setStudent(student);
        result.setTest(test);
        result.setFinished(entity.getFinished());
        result.setPoints(entity.getPoints());
        result.setResultList(resultList);
        result.setGroupExaminationId(entity.getGroupExaminationId());
        result.setTestVersion(version);

        return result;
    }

    private QuestionExamination getQuestionResult(QuestionExaminationEntity it) {
        Question question = questionService.getQuestionById(it.getQuestionId());
        QuestionExamination questionExamination = new QuestionExamination();
        questionExamination.setId(it.getId());
        questionExamination.setQuestion(question);
        questionExamination.setPoints(it.getResultPoints());
        questionExamination.setAnswer(it.getAnswer());
        return questionExamination;
    }


}


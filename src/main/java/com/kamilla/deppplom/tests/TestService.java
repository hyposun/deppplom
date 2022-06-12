package com.kamilla.deppplom.tests;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.repository.DisciplineRepository;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.tests.model.CreateTestRequest;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.tests.model.TestVersion;
import com.kamilla.deppplom.tests.repository.TestRepository;
import com.kamilla.deppplom.tests.repository.TestVersionRepository;
import com.kamilla.deppplom.tests.repository.model.TestEntity;
import com.kamilla.deppplom.tests.repository.model.TestVersionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestVersionRepository testVersionRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private QuestionService questionService;

    public List<Test> findAll() {
        return testRepository.findAll()
                .stream().map(this::fromEntity)
                .collect(toList());
    }

    public List<Test> findAll(int disciplineId) {
        return testRepository.findByDisciplineId(disciplineId)
                .stream().map(this::fromEntity)
                .collect(toList());
    }

    public Test createTest(CreateTestRequest request) {

        verifyCollision(request.getTitle(), request.getDisciplineId());
        getDiscipline(request.getDisciplineId());

        TestEntity entity = new TestEntity();
        entity.setDisciplineId(request.getDisciplineId());
        entity.setTitle(request.getTitle());
        entity.setMinimumPoints(request.getMinimumPoints());
        entity.setLowQuestions(request.getLowQuestions());
        entity.setMediumQuestion(request.getMediumQuestion());
        entity.setHighQuestions(request.getHighQuestions());
        entity = testRepository.save(entity);

        return fromEntity(entity);
    }

    @Transactional
    public Test createRandomizedVariants(int testId, int replicas) {

        var test = getTest(testId);
        var questions = questionService.findQuestionsByDisciplineId(test.getDisciplineId());

        for (int i = 0; i < replicas; i++) {
            var ids = getRandomizedQuestions(test, questions);
            manuallyCreateVersion(ids, test);
        }

        test = testRepository.getOne(test.getId());
        return fromEntity(test);
    }

    public Test manuallyCreateVersion(int testId, List<Integer> questionIds) {
        var test = getTest(testId);
        manuallyCreateVersion(questionIds, test);
        return fromEntity(test);
    }

    public Optional<Test> findById(int testId) {
        return testRepository.findById(testId).map(this::fromEntity);
    }

    private List<Integer> getRandomizedQuestions(TestEntity test, List<Question> questions) {

        var lowTests = getRandomQuestions(test.getLowQuestions(), Difficulty.LOW, questions);
        var mediumTests = getRandomQuestions(test.getMediumQuestion(), Difficulty.MEDIUM, questions);
        var highTests = getRandomQuestions(test.getHighQuestions(), Difficulty.HIGH, questions);

        List<Question> result = new ArrayList<>();
        result.addAll(lowTests);
        result.addAll(mediumTests);
        result.addAll(highTests);
        shuffle(result);

        return result.stream()
                .map(Question::getId)
                .collect(toList());
    }

    private TestVersionEntity manuallyCreateVersion(List<Integer> questionIds, TestEntity test) {
        var variantEntity = new TestVersionEntity();
        variantEntity.setTestId(test.getId());
        variantEntity.setQuestions(questionIds);
        return testVersionRepository.save(variantEntity);
    }

    private List<Question> getRandomQuestions(
        int quantity,
        Difficulty difficulty,
        List<Question> all
    ) {
        var questions = all.stream()
                .filter(it -> it.getDifficulty() == difficulty)
                .collect(toList());

        if (quantity > questions.size()) {
            throw new IllegalStateException("Недостаточное количество вопросов со сложностью " + difficulty);
        }

        Random random = new Random();
        var result = new ArrayList<Question>();
        for (int i = 0; i < quantity; i++) {
            int randomIndex = random.nextInt(questions.size());
            var item = questions.remove(randomIndex);
            result.add(item);
        }
        return result;
    }

    private TestEntity getTest(int testId) {
        return testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Тест не найден"));
    }

    private Test fromEntity(TestEntity test) {

        var versions = testVersionRepository.findByTestId(test.getId()).stream()
                .map(entity -> {
                    var questions = entity.getQuestions();
                    return new TestVersion(entity.getId(), getQuestionsByIds(questions));
                })
                .collect(toList());

        return new Test(
            test.getId(),
            test.getTitle(),
            test.getMinimumPoints(),
            versions,
            getDiscipline(test.getDisciplineId()),
            test.getLowQuestions(),
            test.getMediumQuestion(),
            test.getHighQuestions()
        );
    }


    private Discipline getDiscipline(int id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Дисциплина не найдена"));
    }

    private List<Question> getQuestionsByIds(List<Integer> ids) {
        return ids.stream()
                .map(id -> questionService.findQuestionById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private void verifyCollision(String title, int disciplineId) {
        Optional<TestEntity> existingTest = testRepository.findByTitleAndDisciplineId(title, disciplineId);
        if (existingTest.isPresent()) {
            throw new IllegalArgumentException("Тест c таким именем и дисциплиной уже существует");
        }
    }

    public void delete(int id) {
        testRepository.deleteById(id);
    }
}
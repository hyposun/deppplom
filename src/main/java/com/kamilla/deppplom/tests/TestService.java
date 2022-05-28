package com.kamilla.deppplom.tests;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.repository.DisciplineRepository;
import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.model.Difficulty;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.question.repository.QuestionEntity;
import com.kamilla.deppplom.tests.model.*;
import com.kamilla.deppplom.tests.repository.TestRepository;
import com.kamilla.deppplom.tests.repository.TestVersionRepository;
import com.kamilla.deppplom.tests.repository.model.TestEntity;
import com.kamilla.deppplom.tests.repository.model.TestVersionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Test> findAll(int disciplineId) {
        return testRepository.findByDisciplineId(disciplineId)
                .stream().map(this::fromEntity)
                .collect(toList());
    }

    public Test createTest(CreateTestRequest request) {

        verifyCollision(request);
        getDiscipline(request.getDisciplineId());

        TestEntity entity = new TestEntity();
        entity.setDisciplineId(request.getDisciplineId());
        entity.setTitle(request.getTitle());
        entity.setMinimumPoints(request.getMinimumPoints());
        entity = testRepository.save(entity);

        return fromEntity(entity);
    }

    public Test createRandomizedVariants(CreateRandomizedTestVariantRequest request) {

        var test = getTest(request.getTestId());
        var questions = questionService.findQuestionsByDisciplineId(test.getDisciplineId());

        for (int i = 0; i < request.getReplicas() + 1; i++) {
            var ids = getRandomizedQuestions(request, questions);
            manuallyCreateVersion(ids, test);
        }

        test = testRepository.getOne(test.getId());
        return fromEntity(test);
    }

    public Test manuallyCreateVersion(ManuallyCreateTestVersionRequest request) {
        var test = getTest(request.getTestId());
        test = manuallyCreateVersion(request.getQuestionIds(), test);
        return fromEntity(test);
    }

    public Optional<Test> findById(int testId) {
        return testRepository.findById(testId).map(this::fromEntity);
    }

    private List<Integer> getRandomizedQuestions(CreateRandomizedTestVariantRequest request, List<Question<Selection>> questions) {

        var lowTests = getRandomQuestions(request.getLowQuestions(), Difficulty.LOW, questions);
        var mediumTests = getRandomQuestions(request.getMediumQuestion(), Difficulty.MEDIUM, questions);
        var highTests = getRandomQuestions(request.getHighQuestions(), Difficulty.HIGH, questions);

        List<Question<Selection>> result = new ArrayList<>();
        result.addAll(lowTests);
        result.addAll(mediumTests);
        result.addAll(highTests);
        shuffle(result);

        return result.stream()
                .map(Question::getId)
                .collect(toList());
    }

    private TestEntity manuallyCreateVersion(List<Integer> questionIds, TestEntity test) {
        var variantEntity = new TestVersionEntity();
        variantEntity.setQuestions(toEntities(getQuestionsByIds(questionIds)));
        variantEntity = testVersionRepository.save(variantEntity);

        test.getVersions().add(variantEntity);
        test = testRepository.save(test);
        return test;
    }

    private List<Question<Selection>> getRandomQuestions(
        int quantity,
        Difficulty difficulty,
        List<Question<Selection>> all
    ) {
        var questions = all.stream()
                .filter(it -> it.getDifficulty() == difficulty)
                .collect(toList());

        if (quantity > questions.size()) {
            throw new IllegalStateException("Недостаточное количество вопросов со сложностью " + difficulty);
        }

        Random random = new Random();
        var result = new ArrayList<Question<Selection>>();
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

        var versions = test.getVersions().stream()
                .map(entity -> {
                    var questions = entity.getQuestions().stream().map(QuestionEntity::getId).collect(toList());
                    return new TestVersion(entity.getId(), getQuestionsByIds(questions));
                })
                .collect(toList());

        return new Test(
                test.getId(),
                test.getTitle(),
                test.getMinimumPoints(),
                versions,
                getDiscipline(test.getDisciplineId())
        );
    }


    private Discipline getDiscipline(int id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Дисциплина не найдена"));
    }

    private List<QuestionEntity> toEntities(List<Question<Selection>> questions) {
        return questions.stream().map(question -> {
            var questionEntity = new QuestionEntity();
            questionEntity.setId(question.getId());
            return questionEntity;
        }).collect(toList());
    }

    private List<Question<Selection>> getQuestionsByIds(List<Integer> ids) {
        return ids.stream()
                .map(id -> questionService.findQuestionById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private void verifyCollision(CreateTestRequest request) {
        Optional<TestEntity> existingTest = testRepository.findByTitleAndDisciplineId(request.getTitle(), request.getDisciplineId());
        if (existingTest.isPresent()) {
            throw new IllegalArgumentException("Тест c таким именем и дисциплиной уже существует");
        }
    }
}
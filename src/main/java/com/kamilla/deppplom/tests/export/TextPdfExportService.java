package com.kamilla.deppplom.tests.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.kamilla.deppplom.media.MediaService;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.tests.model.TestVersion;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class TextPdfExportService {

    private static final String FONT_PATH = "src/main/resources/arial.ttf";

    @Autowired
    private MediaService mediaService;

    private Font bigFont;
    private Font mediumFont;
    private Font normalFont;
    private Font smallFont;

    @SneakyThrows
    public TextPdfExportService() {
        BaseFont baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, true);
        bigFont = new Font(baseFont, 18, Font.BOLD);
        mediumFont = new Font(baseFont, 16, Font.NORMAL);
        normalFont = new Font(baseFont, 13, Font.NORMAL, BaseColor.BLACK);
        smallFont = new Font(baseFont, 10, Font.NORMAL, BaseColor.BLACK);
    }

    public void getPdfDocument(ByteArrayOutputStream byteArrayOutputStream, Test test, TestVersion version) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            generateContent(document, test, version);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateContent(Document document, Test test, TestVersion version) throws DocumentException {

        Paragraph header = new Paragraph("Тест '" + test.getTitle() + "'", bigFont);
        addEmptyLine(header, 2);
        document.add(header);

        Paragraph questions = new Paragraph("Вопросы", bigFont);
        addEmptyLine(questions, 2);
        document.add(questions);

        addQuestions(version, document);
        document.newPage();
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" ", normalFont));
        }
    }

    private void addQuestions(TestVersion version, Document document) throws DocumentException {
        var questionList = version.getQuestions();
        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);

            Paragraph titleP = new Paragraph((i + 1) + "." + " " + question.getTitle(), mediumFont);
            document.add(titleP);

            if (question.getDescription() != null && !isBlank(question.getDescription())) {
                document.add(new Paragraph(question.getDescription(), normalFont));
            }

            addQuestionTypeContent(question, document);

            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph(" ", normalFont));
        }
    }

    private void addQuestionTypeContent(Question question, Document document) throws DocumentException {
        switch (question.getType()) {
            case OPENED: {
                document.add(new Paragraph(" ", normalFont));
                Paragraph paragraph = new Paragraph("Ответ: ", smallFont);
                addEmptyLine(paragraph, 1);
                document.add(paragraph);
                break;
            }
            case CLOSED: {
                var closedQuestion = (ClosedQuestion) question;
                addClosedQuestionContent(closedQuestion, document);
                break;
            }
            case CLOSED_ORDERED: {
                var orderedClosedQuestion = (OrderedClosedQuestion) question;
                addOrderedClosedQuestionContent(orderedClosedQuestion, document);
                break;
            }
        }
    }

    private void addClosedQuestionContent(ClosedQuestion question, Document document) throws DocumentException {
        List list = new List();
        for (int i = 0; i < question.getOptions().size(); i++) {
            ClosedQuestion.Option option = question.getOptions().get(i);

            ListItem item = new ListItem((i + 1) + ". " + option.getTitle(), smallFont);
            list.add(item);

            if (option.getImageMediaId() != null) {
                Jpeg image = getImage(option.getImageMediaId());
                if (image != null) document.add(image);
            }
        }
        list.setIndentationLeft(10);
        list.setListSymbol(new Chunk("", smallFont));

        document.add(new Paragraph(" ", smallFont));
        document.add(list);
        document.add(new Paragraph(" ", normalFont));
        document.add(new Paragraph("Номера ответов: ", smallFont));
        document.add(new Paragraph(" ", normalFont));
    }

    @SneakyThrows
    private Jpeg getImage(int imageMediaId) {

        var content = mediaService
                .findById(imageMediaId)
                .map(media -> mediaService.download(media.getId()));

        if (content.isEmpty()) return null;
        return new Jpeg(content.get());
    }

    private void addOrderedClosedQuestionContent(OrderedClosedQuestion question, Document document) throws DocumentException {
        List list = new List();
        for (int i = 0; i < question.getOptions().size(); i++) {
            OrderedClosedQuestion.Option option = question.getOptions().get(i);
            ListItem item = new ListItem((i + 1) + ". " + option.getTitle(), smallFont);
            list.add(item);
        }
        list.setIndentationLeft(10);
        list.setListSymbol(new Chunk("", smallFont));
        document.add(new Paragraph(" ", smallFont));
        document.add(list);
        document.add(new Paragraph(" ", normalFont));
        document.add(new Paragraph("Номера ответов в правильном порядке: ", smallFont));
        document.add(new Paragraph(" ", normalFont));
    }

}
package com.kamilla.deppplom.tests.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.openquestion.OpenQuestion;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.tests.model.TestVersion;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class TextPdfExport {

    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

    public void getPdfDocument(ByteArrayOutputStream byteArrayOutputStream, TestVersion version) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            addPage(document,version);
            document.close();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    private void addPage(Document document,TestVersion version) throws DocumentException {

        addContent(version,document);
        document.newPage();
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void addContent(TestVersion version,Document document) throws DocumentException {

        Paragraph paragraph = new Paragraph();
        paragraph.add(new Paragraph("Экзаменационный билет: ", subFont));
        addEmptyLine(paragraph,5);
        document.add(paragraph);

        var questionList = version.getQuestions();
        int i=1;
        for (Question question : questionList) {
            Paragraph questionPar = new Paragraph(i + "." + " " + question.getTitle(), normalFont);
            getQuestionTypeContent(question,document);
            document.add(questionPar);
            i++;
        }
    }

    private void getQuestionTypeContent(Question question, Document document) throws DocumentException {
        var questionType = question.getType();
        switch (questionType) {
            case OPENED: {
                var openQuestion = (OpenQuestion)question;
                Phrase phrase = new Phrase("\n");
                document.add(phrase);
                break;
            }
            case CLOSED: {
                var closedQuestion = (ClosedQuestion)question;
                getClosedQuestionAnswer(closedQuestion, document);
                break;
            }
            case CLOSED_ORDERED: {
                var orderedClosedQuestion = (OrderedClosedQuestion)question;
                getOrderedClosedQuestionAnswer(orderedClosedQuestion, document);
                break;
            }
        }
    }

    private void getClosedQuestionAnswer(ClosedQuestion question, Document document) throws DocumentException {
        var answers = question.getOptions();
        int i = 1;
        for (ClosedQuestion.Option answer : answers) {
            Paragraph answerTitle = new Paragraph(i + "." + " " + answer.getTitle(),normalFont);
            answerTitle.setIndentationLeft(50);
            document.add(answerTitle);
            i++;
        }
    }

    private void getOrderedClosedQuestionAnswer(OrderedClosedQuestion question, Document document) throws DocumentException {
        var answers = question.getOptions();
        int i = 1;
        for (OrderedClosedQuestion.Option answer : answers) {
            Paragraph answerTitle = new Paragraph( i+ "." + " " + answer.getTitle(),normalFont);
            answerTitle.setIndentationLeft(50);
            document.add(answerTitle);
            i++;
        }
    }

}

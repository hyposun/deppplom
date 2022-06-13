package com.kamilla.deppplom.tests.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.tests.model.TestVersion;

import java.io.FileOutputStream;

public class TestPdfExport {

    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

    private void getPdfDocument(String path, TestVersion version) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            addPage(document);
            addContent(version);
            document.close();
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    private void addPage(Document document) throws DocumentException {

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph,1);
        paragraph.add(new Paragraph("Экзаменационный билет: ", subFont));
        addEmptyLine(paragraph,1);
        paragraph.add(new Paragraph("Вопросы: ", subFont));
        addEmptyLine(paragraph, 3);

        document.add(paragraph);

        document.newPage();


    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void addContent(TestVersion version){
        var questionList = version.getQuestions();
        for (Question question : questionList) {
            int i=1;
            Paragraph questionPar = new Paragraph(i + question.getDescription(), normalFont);
            addEmptyLine(questionPar,1);
            i++;
        }
    }

}

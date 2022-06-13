package com.kamilla.deppplom.ui.teacher.test;

import com.kamilla.deppplom.tests.export.TextPdfExport;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.tests.model.TestVersion;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DownloadVersionPdfLink extends Anchor {

    private byte[] content;

    public DownloadVersionPdfLink(TextPdfExport textPdfExport, Test test, TestVersion testVersion) {

        String fileName = test.getTitle().replace(" ", "_") + "_version-" + testVersion.getId() + ".pdf";

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            textPdfExport.getPdfDocument(outputStream, testVersion);
            content = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Anchor anchor = new Anchor(getStreamResource(fileName), "скачать");
        anchor.getElement().setAttribute("download", true);
        anchor.setHref(getStreamResource(fileName));
        add(anchor);
    }

    public StreamResource getStreamResource(String filename) {
        return new StreamResource(filename, () -> new BufferedInputStream(new ByteArrayInputStream(content)));
    }

}

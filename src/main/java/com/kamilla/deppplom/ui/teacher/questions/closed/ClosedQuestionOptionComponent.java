package com.kamilla.deppplom.ui.teacher.questions.closed;

import com.kamilla.deppplom.media.MediaService;
import com.kamilla.deppplom.media.model.Media;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static java.lang.Integer.parseInt;

public class ClosedQuestionOptionComponent extends VerticalLayout {

    private MediaService mediaService;
    private Integer imageMediaId;

    private TextField id = new TextField("ID");
    private TextField title = new TextField("Ответ");
    private Checkbox valid = new Checkbox("Правильный ответ");
    private Button removeButton = new Button("Удалить", VaadinIcon.TRASH.create());
    private Button removeImageButton = new Button(VaadinIcon.TRASH.create());

    private Image image = new Image();
    private MemoryBuffer memoryBuffer = new MemoryBuffer();
    private Upload imageUpload = new Upload(memoryBuffer);

    @Setter
    private Runnable onDelete;

    public ClosedQuestionOptionComponent(
        int id,
        String title,
        boolean valid,
        Integer imageMediaId,
        MediaService mediaService
    ) {
        this.mediaService = mediaService;
        this.imageMediaId = imageMediaId;
        this.id.setValue(String.valueOf(id));
        this.id.setReadOnly(true);
        this.id.setWidth("50px");

        if (title != null) this.title.setValue(title);
        this.valid.setValue(valid);

        setWidthFull();

        setupImageComponents();

        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        removeImageButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setVerticalComponentAlignment(Alignment.BASELINE);
        horizontalLayout.setAlignItems(Alignment.BASELINE);
        horizontalLayout.add(this.id, this.title, this.valid, imageUpload, removeButton);
        add(horizontalLayout);
        add(image, removeImageButton);

        removeButton.addClickListener(event -> onDelete.run());
    }

    private void setupImageComponents() {

        image.setMaxWidth("150px");

        removeImageButton.addClickListener(event -> {
            if (imageMediaId != null) {
                mediaService.remove(imageMediaId);
                imageMediaId = null;
                refreshImage();
            }
        });

        imageUpload.setUploadButton(new Button("Загрузить изображение", VaadinIcon.FILE.create()));
        imageUpload.setAcceptedFileTypes(".jpg");
        imageUpload.setMaxFiles(1);
        imageUpload.setDropAllowed(false);
        imageUpload.addSucceededListener(event -> uploadImage());

        refreshImage();
    }

    private void refreshImage() {
        Optional<Media> maybeMedia = Optional.ofNullable(imageMediaId).flatMap(id -> mediaService.findById(id));
        if (maybeMedia.isEmpty()) {
            image.setVisible(false);
            removeImageButton.setVisible(false);
            return;
        }

        Media media = maybeMedia.get();
        byte[] content = mediaService.download(media.getId());
        image.setSrc(new StreamResource(media.getName(), () -> new ByteArrayInputStream(content)));
        image.setVisible(true);
        removeImageButton.setVisible(true);
    }

    private void uploadImage() {
        try {
            Media media = new Media(0, null, memoryBuffer.getFileName());
            media = mediaService.upload(media, memoryBuffer.getInputStream());
            imageMediaId = media.getId();
            refreshImage();
            imageUpload.clearFileList();
        } catch (Exception e) {
            imageUpload.clearFileList();
            errorNotification("Не удалось загрузить изображение: " + e.getMessage(), 2);
        }
    }

    public Option getOption() {
        return new Option(parseInt(id.getValue()), title.getValue(), valid.getValue(), imageMediaId);
    }

    @Data
    @AllArgsConstructor
    public static class Option {

        private int id;
        private String title;
        private boolean valid;
        private Integer imageMediaId;

        public boolean isEmpty() {
            return title == null || title.isBlank();
        }

    }


}

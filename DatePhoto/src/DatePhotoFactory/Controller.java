package DatePhotoFactory;

import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.List;

public class Controller {
    private ImageProcessorModel model;
    private View view;

    public Controller(ImageProcessorModel model, View view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        view.getAddButton().addActionListener(e -> addFile());
        view.getRemoveButton().addActionListener(e -> removeFile());
        view.getProcessButton().addActionListener(e -> processFiles());
    }

    private void addFile() {
        File[] selectedFiles = view.showFileChooser();
        if (selectedFiles != null) {
            view.addFilesToList(selectedFiles);
        }
    }

    private void removeFile() {
        File selectedFile = view.getSelectedFile();
        if (selectedFile != null) {
            view.removeFileFromList(selectedFile);
        } else {
            view.showErrorMessage("No file selected for removal.");
        }
    }

    private void processFiles() {
        List<File> files = view.getFileList();
        Date date = view.getSelectedDate();
        String font = view.getSelectedFont();
        int fontSize = view.getSelectedFontSize();
        Color color = view.getSelectedColor();
        String format = view.getSelectedFormat();

        if (files.isEmpty()) {
            view.showErrorMessage("No files to process.");
            return;
        }

        model.processImages(files, date, font, fontSize, color, format, progress -> view.updateProgress(progress));
    }
}


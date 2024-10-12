package DatePhotoMVC;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatePhotoController {
    private ImageProcessorModel model;
    private DatePhotoView view;

    public DatePhotoController(ImageProcessorModel model, DatePhotoView view) {
        this.model = model;
        this.view = view;

        // Event handling for adding files
        view.getAddButton().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                for (File file : fileChooser.getSelectedFiles()) {
                    view.getFileListModel().addElement(file);
                }
            }
        });

        // Event handling for removing files
        view.getRemoveButton().addActionListener(e -> {
            int selectedIndex = view.getFileList().getSelectedIndex();
            if (selectedIndex != -1) {
                view.getFileListModel().remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(null, "Select a file to remove.");
            }
        });

        // Event handling for processing images
        view.getProcessButton().addActionListener(e -> {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            Date selectedDate = (Date) view.getDateSpinner().getValue();
            String selectedFont = (String) view.getFontComboBox().getSelectedItem();
            int selectedFontSize = (int) view.getFontSizeSpinner().getValue();
            Color selectedColor = getColorFromComboBox();

            for (int i = 0; i < view.getFileListModel().size(); i++) {
                File inputFile = view.getFileListModel().getElementAt(i);
                executor.submit(() -> model.processImage(inputFile, selectedDate, selectedFont, selectedFontSize, selectedColor));
            }

            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(null, "All images processed.");
        });
    }

    // Get color based on the JComboBox selection
    private Color getColorFromComboBox() {
        String selectedColor = (String) view.getColorComboBox().getSelectedItem();
        switch (selectedColor) {
            case "Black":
                return Color.BLACK;
            case "Red":
                return Color.RED;
            case "Green":
                return Color.GREEN;
            case "Blue":
                return Color.BLUE;
            default:
                return Color.WHITE;
        }
    }
}


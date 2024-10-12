package DatePhotoMVC;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Date;

public class DatePhotoView extends JFrame {
    private DefaultListModel<File> fileListModel;
    private JList<File> fileList;
    private JSpinner dateSpinner;
    private JComboBox<String> fontComboBox;
    private JSpinner fontSizeSpinner;
    private JComboBox<String> colorComboBox;
    private JButton addButton;
    private JButton removeButton;
    private JButton processButton;

    public DatePhotoView() {
        setTitle("Add Date to Photo");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(200, 100));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        addButton = new JButton("Add File");
        removeButton = new JButton("Remove File");
        processButton = new JButton("Add Date");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(processButton);

        JPanel datePanel = new JPanel();
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "HH:mm dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        datePanel.add(new JLabel("Date and Time:"));
        datePanel.add(dateSpinner);

        JPanel fontPanel = new JPanel();
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontComboBox.setSelectedItem("Arial");
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 500, 1));
        fontPanel.add(new JLabel("Font:"));
        fontPanel.add(fontComboBox);
        fontPanel.add(new JLabel("Size:"));
        fontPanel.add(fontSizeSpinner);

        JPanel colorPanel = new JPanel();
        colorComboBox = new JComboBox<>(new String[]{"White", "Black", "Red", "Green", "Blue"});
        colorPanel.add(new JLabel("Color:"));
        colorPanel.add(colorComboBox);

        JPanel settingsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        settingsPanel.add(datePanel);
        settingsPanel.add(fontPanel);
        settingsPanel.add(colorPanel);

        add(scrollPane, BorderLayout.NORTH);
        add(settingsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getters for the UI components
    public JList<File> getFileList() {
        return fileList;
    }

    public DefaultListModel<File> getFileListModel() {
        return fileListModel;
    }

    public JSpinner getDateSpinner() {
        return dateSpinner;
    }

    public JComboBox<String> getFontComboBox() {
        return fontComboBox;
    }

    public JSpinner getFontSizeSpinner() {
        return fontSizeSpinner;
    }

    public JComboBox<String> getColorComboBox() {
        return colorComboBox;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public JButton getProcessButton() {
        return processButton;
    }
}

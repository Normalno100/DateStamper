package DatePhotoFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class View extends JFrame {
    private DefaultListModel<File> fileListModel;
    private JList<File> fileList;
    private JSpinner dateSpinner;
    private JComboBox<String> fontComboBox, colorComboBox, formatComboBox;
    private JSpinner fontSizeSpinner;
    private JProgressBar progressBar;
    private JButton addButton, removeButton, processButton;

    public View() {
        setTitle("Add Date to Photo");
        setSize(600, 400); // Немного увеличим окно
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрирование окна
        setLayout(new BorderLayout(10, 10)); // Используем отступы для лучшей компоновки

        // Модель списка файлов
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(300, 150)); // Задаем размер для списка файлов

        // Панель для кнопок
        addButton = new JButton("Add File");
        removeButton = new JButton("Remove File");
        processButton = new JButton("Add Date");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Центрируем кнопки
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(processButton);

        // Настройка панели для выбора даты
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "HH:mm dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Date and Time:"));
        datePanel.add(dateSpinner);

        // Настройка панели для выбора шрифта и размера шрифта
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontComboBox.setSelectedItem("Arial");

        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 500, 1));

        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontPanel.add(new JLabel("Font:"));
        fontPanel.add(fontComboBox);
        fontPanel.add(new JLabel("Size:"));
        fontPanel.add(fontSizeSpinner);

        // Настройка панели для выбора цвета текста
        colorComboBox = new JComboBox<>(new String[]{"White", "Black", "Red", "Green", "Blue"});
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.add(new JLabel("Text Color:"));
        colorPanel.add(colorComboBox);

        // Настройка панели для выбора формата файла
        formatComboBox = new JComboBox<>(new String[]{"JPG", "PNG", "BMP"});
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.add(new JLabel("Output Format:"));
        formatPanel.add(formatComboBox);

        // Прогресс бар
        progressBar = new JProgressBar(0, 100);

        // Главная панель для настроек
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS)); // Вертикальное расположение элементов
        settingsPanel.add(datePanel);
        settingsPanel.add(fontPanel);
        settingsPanel.add(colorPanel);
        settingsPanel.add(formatPanel);

        // Компоновка всех частей окна
        add(scrollPane, BorderLayout.WEST); // Список файлов слева
        add(settingsPanel, BorderLayout.CENTER); // Панель настроек по центру
        add(buttonPanel, BorderLayout.SOUTH); // Кнопки внизу
        add(progressBar, BorderLayout.NORTH); // Прогресс бар сверху
    }

    // Getters for components
    public JButton getAddButton() { return addButton; }
    public JButton getRemoveButton() { return removeButton; }
    public JButton getProcessButton() { return processButton; }
    public ArrayList<File> getFileList() {
        return Collections.list(fileListModel.elements());
    }
    public Date getSelectedDate() { return (Date) dateSpinner.getValue(); }
    public String getSelectedFont() { return (String) fontComboBox.getSelectedItem(); }
    public int getSelectedFontSize() { return (int) fontSizeSpinner.getValue(); }
    public Color getSelectedColor() {
        String color = (String) colorComboBox.getSelectedItem();
        switch (color) {
            case "Black": return Color.BLACK;
            case "Red": return Color.RED;
            case "Green": return Color.GREEN;
            case "Blue": return Color.BLUE;
            default: return Color.WHITE;
        }
    }
    public String getSelectedFormat() { return (String) formatComboBox.getSelectedItem(); }

    public void addFilesToList(File[] files) {
        for (File file : files) {
            fileListModel.addElement(file);
        }
    }

    public void removeFileFromList(File file) {
        fileListModel.removeElement(file);
    }

    public File getSelectedFile() {
        return fileList.getSelectedValue();
    }

    public void updateProgress(int progress) {
        progressBar.setValue(progress);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public File[] showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFiles();
        }
        return null;
    }
}


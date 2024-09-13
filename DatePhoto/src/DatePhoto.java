//Идеи улучшений:
// 1. Окно предпрсмотра
// 2. Каждую фичу выделить в отдельный модуль

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatePhoto extends JFrame {
    private DefaultListModel<File> fileListModel;
    private JList<File> fileList;
    private JSpinner dateSpinner;
    private JComboBox<String> fontComboBox; // Для выбора шрифта
    private JSpinner fontSizeSpinner; // Для выбора размера шрифта
    private JComboBox<String> colorComboBox; // Для выбора цвета

    public DatePhoto() {
        setTitle("Добавление даты на фото");
        setSize(450, 350); // Размер окна 400x300
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Модель для списка файлов
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(200, 100));

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // Три кнопки: добавить, удалить, обработать
        JButton addButton = new JButton("Добавить файл");
        JButton removeButton = new JButton("Удалить файл"); // Новая кнопка для удаления
        JButton processButton = new JButton("Добавить дату");

        // Добавление файлов
        addButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Изображения", "jpg", "jpeg", "png"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                for (File file : fileChooser.getSelectedFiles()) {
                    fileListModel.addElement(file);
                }
            }
        });

        // Удаление выбранного файла
        removeButton.addActionListener(e -> {
            int selectedIndex = fileList.getSelectedIndex();
            if (selectedIndex != -1) {
                fileListModel.remove(selectedIndex); // Удаление выбранного файла из списка
            } else {
                JOptionPane.showMessageDialog(null, "Выберите файл для удаления.");
            }
        });

        // Обработка файлов
        processButton.addActionListener(e -> {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            Date selectedDate = (Date) dateSpinner.getValue();
            String selectedFont = (String) fontComboBox.getSelectedItem(); // Получение выбранного шрифта
            int selectedFontSize = (int) fontSizeSpinner.getValue(); // Получение размера шрифта
            Color selectedColor = getColorFromComboBox(); // Получение цвета

            for (int i = 0; i < fileListModel.size(); i++) {
                File inputFile = fileListModel.getElementAt(i);
                executor.submit(() -> processImage(inputFile, selectedDate, selectedFont, selectedFontSize, selectedColor));
            }
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Все изображения обработаны.");
        });

        // Добавление кнопок в панель
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton); // Добавлена кнопка удаления
        buttonPanel.add(processButton);

        // Панель для выбора даты и времени
        JPanel datePanel = new JPanel();
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "HH:mm dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date()); // текущее время по умолчанию
        datePanel.add(new JLabel("Дата и время:"));
        datePanel.add(dateSpinner);

        // Панель для выбора шрифта и размера шрифта
        JPanel fontPanel = new JPanel();
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontComboBox.setSelectedItem("Arial"); // По умолчанию Arial
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 500, 1)); // Размер шрифта от 10 до 100
        fontPanel.add(new JLabel("Шрифт:"));
        fontPanel.add(fontComboBox);
        fontPanel.add(new JLabel("Размер:"));
        fontPanel.add(fontSizeSpinner);

        // Панель для выбора цвета
        JPanel colorPanel = new JPanel();
        colorComboBox = new JComboBox<>(new String[]{"Белый", "Черный", "Красный", "Зеленый", "Синий"});
        colorPanel.add(new JLabel("Цвет:"));
        colorPanel.add(colorComboBox);

        // Основная панель для ввода данных
        JPanel settingsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        settingsPanel.add(datePanel);
        settingsPanel.add(fontPanel);
        settingsPanel.add(colorPanel);

        // Добавляем компоненты в главное окно
        add(scrollPane, BorderLayout.NORTH); // Список файлов сверху
        add(settingsPanel, BorderLayout.CENTER); // Панель настроек в центре
        add(buttonPanel, BorderLayout.SOUTH); // Панель с кнопками снизу
    }

    private static void processImage(File inputFile, Date selectedDate, String selectedFont, int selectedFontSize, Color selectedColor) {
        try {
            // Загрузка изображения
            BufferedImage image = ImageIO.read(inputFile);

            // Форматирование выбранной даты
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            String date = sdf.format(selectedDate);

            // Добавление даты на изображение
            BufferedImage imageWithDate = addDateToImage(image, date, selectedFont, selectedFontSize, selectedColor);

            // Сохранение нового изображения
            File outputFile = new File("output_" + inputFile.getName());
            ImageIO.write(imageWithDate, "jpg", outputFile);

            System.out.println("Дата добавлена на фото " + inputFile.getName() + " и сохранена как " + outputFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage addDateToImage(BufferedImage image, String date, String selectedFont, int selectedFontSize, Color selectedColor) {
        Graphics2D g2d = image.createGraphics();

        // Настройка шрифта и цвета
        Font font = new Font(selectedFont, Font.BOLD, selectedFontSize);
        g2d.setFont(font);
        g2d.setColor(selectedColor);

        // Получение метрик шрифта для вычисления положения текста
        FontMetrics fm = g2d.getFontMetrics();

        // Положение текста
        int x = image.getWidth() - fm.stringWidth(date) - 10;
        int y = image.getHeight() - fm.getDescent() - 10;

        // Рисование текста
        g2d.drawString(date, x, y);
        g2d.dispose();

        return image;
    }

    // Метод для получения цвета из JComboBox
    private Color getColorFromComboBox() {
        String selectedColor = (String) colorComboBox.getSelectedItem();
        switch (selectedColor) {
            case "Черный":
                return Color.BLACK;
            case "Красный":
                return Color.RED;
            case "Зеленый":
                return Color.GREEN;
            case "Синий":
                return Color.BLUE;
            default:
                return Color.WHITE;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatePhoto frame = new DatePhoto();
            frame.setVisible(true);
        });
    }
}

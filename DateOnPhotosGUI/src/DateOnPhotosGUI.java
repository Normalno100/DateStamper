import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DateOnPhotosGUI extends JFrame {
    private DefaultListModel<File> fileListModel;
    private JList<File> fileList;
    private JSpinner dateSpinner;

    public DateOnPhotosGUI() {
        setTitle("Добавление даты на фото");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);

        JButton addButton = new JButton("Добавить файлы");
        JButton processButton = new JButton("Добавить дату на фото");

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

        processButton.addActionListener(e -> {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            Date selectedDate = (Date) dateSpinner.getValue();
            for (int i = 0; i < fileListModel.size(); i++) {
                File inputFile = fileListModel.getElementAt(i);
                executor.submit(() -> processImage(inputFile, selectedDate));
            }
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Все изображения обработаны.");
        });

        // Панель для выбора даты и времени
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BorderLayout());
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "HH:mm dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date()); // текущее время по умолчанию
        datePanel.add(new JLabel("Выберите дату и время:"), BorderLayout.WEST);
        datePanel.add(dateSpinner, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(fileList), BorderLayout.CENTER);
        panel.add(datePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(processButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private static void processImage(File inputFile, Date selectedDate) {
        try {
            // Загрузка изображения
            BufferedImage image = ImageIO.read(inputFile);

            // Форматирование выбранной даты
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            String date = sdf.format(selectedDate);

            // Добавление даты на изображение
            BufferedImage imageWithDate = addDateToImage(image, date);

            // Сохранение нового изображения
            File outputFile = new File("output_" + inputFile.getName());
            ImageIO.write(imageWithDate, "jpg", outputFile);

            System.out.println("Дата добавлена на фото " + inputFile.getName() + " и сохранена как " + outputFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage addDateToImage(BufferedImage image, String date) {
        Graphics2D g2d = image.createGraphics();

        // Настройка шрифта и цвета
        //Font font = new Font("Arial", Font.BOLD, 20);
        int fontSize = image.getHeight() / 20; // 5% от высоты изображения
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DateOnPhotosGUI frame = new DateOnPhotosGUI();
            frame.setVisible(true);
        });
    }
}
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DateOnPhotos {
    public static void main(String[] args) {
        // Массив файлов изображений для обработки
        File[] inputFiles = {
                new File("input1.jpg"),
                new File("input2.jpg"),
                new File("input3.jpg")
        };

        // Создаем пул потоков
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (File inputFile : inputFiles) {
            executor.submit(() -> processImage(inputFile));
        }

        // Завершаем работу пула потоков
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Все изображения обработаны.");
    }

    private static void processImage(File inputFile) {
        try {
            // Загрузка изображения
            BufferedImage image = ImageIO.read(inputFile);

            // Получение даты
            String date = getPhotoDate(inputFile);

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

    private static String getPhotoDate(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM dd-MM-yyyy");
        return sdf.format(new Date());
    }

    private static BufferedImage addDateToImage(BufferedImage image, String date) {
        Graphics2D g2d = image.createGraphics();

        // Настройка шрифта и цвета
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.BLACK);

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
}

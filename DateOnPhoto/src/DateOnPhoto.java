import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOnPhoto {
    public static void main(String[] args) {
        File inputFile = new File("C:\\Users\\User\\Desktop\\Normalno\\DateStamper\\DateOnPhoto\\input.jpeg");
        File outputFile = new File("output.jpg");

        try {
            // Загрузка изображения
            BufferedImage image = ImageIO.read(inputFile);

            // Получение текущей даты
            String date = getCurrentDate();

            // Добавление даты на изображение
            BufferedImage imageWithDate = addDateToImage(image, date);

            // Сохранение нового изображения
            ImageIO.write(imageWithDate, "jpg", outputFile);

            System.out.println("Дата добавлена на фото и сохранена как " + outputFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM dd-MM-yyyy");
        return sdf.format(new Date());
    }

    private static BufferedImage addDateToImage(BufferedImage image, String date) {
        Graphics2D g2d = image.createGraphics();

        // Настройка шрифта и цвета
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);

        // Положение текста
        int x = 420;
        int y = image.getHeight() - 10;

        // Рисование текста
        g2d.drawString(date, x, y);
        g2d.dispose();

        return image;
    }
}

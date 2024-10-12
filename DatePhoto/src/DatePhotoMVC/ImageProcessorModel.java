package DatePhotoMVC;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageProcessorModel {

    // Process the image by adding the date to it
    public void processImage(File inputFile, Date selectedDate, String selectedFont, int selectedFontSize, Color selectedColor) {
        try {
            BufferedImage image = ImageIO.read(inputFile);

            // Format the selected date
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            String date = sdf.format(selectedDate);

            // Add the date to the image
            BufferedImage imageWithDate = addDateToImage(image, date, selectedFont, selectedFontSize, selectedColor);

            // Save the new image
            File outputFile = new File("new_" + inputFile.getName());
            ImageIO.write(imageWithDate, "jpg", outputFile);

            System.out.println("Date added to photo " + inputFile.getName() + " and saved as " + outputFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add date text to the image
    private BufferedImage addDateToImage(BufferedImage image, String date, String selectedFont,
                                         int selectedFontSize, Color selectedColor) {
        Graphics2D g2d = image.createGraphics();
        Font font = new Font(selectedFont, Font.BOLD, selectedFontSize);
        g2d.setFont(font);
        g2d.setColor(selectedColor);

        FontMetrics fm = g2d.getFontMetrics();
        int x = image.getWidth() - fm.stringWidth(date) - 10;
        int y = image.getHeight() - fm.getDescent() - 10;

        g2d.drawString(date, x, y);
        g2d.dispose();

        return image;
    }
}


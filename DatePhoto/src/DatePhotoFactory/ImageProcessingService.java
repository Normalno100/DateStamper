package DatePhotoFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageProcessingService {
    public BufferedImage addDateToImage(BufferedImage image, Date date, String font, int fontSize, Color color) {
        Graphics2D g2d = image.createGraphics();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String dateString = sdf.format(date);

        Font fontObj = new Font(font, Font.BOLD, fontSize);
        g2d.setFont(fontObj);
        g2d.setColor(color);

        FontMetrics fm = g2d.getFontMetrics();
        int x = image.getWidth() - fm.stringWidth(dateString) - 10;
        int y = image.getHeight() - fm.getDescent() - 10;

        g2d.drawString(dateString, x, y);
        g2d.dispose();

        return image;
    }
}

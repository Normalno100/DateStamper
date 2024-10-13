package DatePhotoFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileService {
    public BufferedImage loadImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    public void saveImage(BufferedImage image, File file, String format) throws IOException {
        ImageIO.write(image, format, file);
    }

    public File createOutputFile(File inputFile, String format) {
        String outputFileName = "new_" + inputFile.getName().replaceFirst("[.][^.]+$", "") + "." + format.toLowerCase();
        return new File(outputFileName);
    }
}

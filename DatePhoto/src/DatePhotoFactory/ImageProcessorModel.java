package DatePhotoFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ImageProcessorModel {
    private FileService fileService;
    private ImageProcessingService imageProcessingService;

    public ImageProcessorModel(FileService fileService, ImageProcessingService imageProcessingService) {
        this.fileService = fileService;
        this.imageProcessingService = imageProcessingService;
    }

    public void processImages(List<File> files, Date date, String font, int fontSize, Color color, String format, Consumer<Integer> onProgress) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        int totalFiles = files.size();

        for (int i = 0; i < totalFiles; i++) {
            File inputFile = files.get(i);
            int progress = i + 1;
            executor.submit(() -> {
                try {
                    BufferedImage image = fileService.loadImage(inputFile);
                    BufferedImage processedImage = imageProcessingService.addDateToImage(image, date, font, fontSize, color);
                    File outputFile = fileService.createOutputFile(inputFile, format);
                    fileService.saveImage(processedImage, outputFile, format);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    onProgress.accept(progress * 100 / totalFiles);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


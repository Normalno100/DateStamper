package DatePhotoFactory;

import javax.swing.*;

public class AppFactory {
    public static void main(String[] args) {
        FileService fileService = new FileService();
        ImageProcessingService imageProcessingService = new ImageProcessingService();
        ImageProcessorModel model = new ImageProcessorModel(fileService, imageProcessingService);
        View view = new View();
        Controller controller = new Controller(model, view);

        SwingUtilities.invokeLater(() -> view.setVisible(true));
    }
}


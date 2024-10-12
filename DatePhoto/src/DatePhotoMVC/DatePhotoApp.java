package DatePhotoMVC;

import javax.swing.*;

public class DatePhotoApp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            ImageProcessorModel model = new ImageProcessorModel();
            DatePhotoView view = new DatePhotoView();
            DatePhotoController controller = new DatePhotoController(model, view);
            view.setVisible(true);
        });
    }
}

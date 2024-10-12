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

public class DatePhotoENG extends JFrame {
    private DefaultListModel<File> fileListModel;
    private JList<File> fileList;
    private JSpinner dateSpinner;
    private JComboBox<String> fontComboBox; // For font selection
    private JSpinner fontSizeSpinner; // For font size selection
    private JComboBox<String> colorComboBox; // For color selection

    public DatePhotoENG() {
        setTitle("Add Date to Photo");
        setSize(450, 350); // Window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Model for file list
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(200, 100));

        // Panel with buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton addButton = new JButton("Add File");
        JButton removeButton = new JButton("Remove File");
        JButton processButton = new JButton("Add Date");

        // Adding files
        addButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                for (File file : fileChooser.getSelectedFiles()) {
                    fileListModel.addElement(file);
                }
            }
        });

        // Removing selected file
        removeButton.addActionListener(e -> {
            int selectedIndex = fileList.getSelectedIndex();
            if (selectedIndex != -1) {
                fileListModel.remove(selectedIndex); // Remove the selected file from the list
            } else {
                JOptionPane.showMessageDialog(null, "Select a file to remove.");
            }
        });

        // Processing files
        processButton.addActionListener(e -> {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            Date selectedDate = (Date) dateSpinner.getValue();
            String selectedFont = (String) fontComboBox.getSelectedItem(); // Get selected font
            int selectedFontSize = (int) fontSizeSpinner.getValue(); // Get font size
            Color selectedColor = getColorFromComboBox(); // Get color

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
            JOptionPane.showMessageDialog(null, "All images processed.");
        });

        // Adding buttons to the panel
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(processButton);

        // Panel for date and time selection
        JPanel datePanel = new JPanel();
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "HH:mm dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date()); // Default to current time
        datePanel.add(new JLabel("Date and Time:"));
        datePanel.add(dateSpinner);

        // Panel for font and font size selection
        JPanel fontPanel = new JPanel();
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontComboBox.setSelectedItem("Arial"); // Default font is Arial
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 500, 1)); // Font size from 10 to 500
        fontPanel.add(new JLabel("Font:"));
        fontPanel.add(fontComboBox);
        fontPanel.add(new JLabel("Size:"));
        fontPanel.add(fontSizeSpinner);

        // Panel for color selection
        JPanel colorPanel = new JPanel();
        colorComboBox = new JComboBox<>(new String[]{"White", "Black", "Red", "Green", "Blue"});
        colorPanel.add(new JLabel("Color:"));
        colorPanel.add(colorComboBox);

        // Main panel for input settings
        JPanel settingsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        settingsPanel.add(datePanel);
        settingsPanel.add(fontPanel);
        settingsPanel.add(colorPanel);

        // Add components to the main window
        add(scrollPane, BorderLayout.NORTH); // File list at the top
        add(settingsPanel, BorderLayout.CENTER); // Settings panel in the center
        add(buttonPanel, BorderLayout.SOUTH); // Buttons panel at the bottom
    }

    private static void processImage(File inputFile, Date selectedDate,
                                     String selectedFont, int selectedFontSize, Color selectedColor) {
        try {
            // Load image
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

    private static BufferedImage addDateToImage(BufferedImage image, String date, String selectedFont,
                                                int selectedFontSize, Color selectedColor) {
        Graphics2D g2d = image.createGraphics();

        // Set font and color
        Font font = new Font(selectedFont, Font.BOLD, selectedFontSize);
        g2d.setFont(font);
        g2d.setColor(selectedColor);

        // Get font metrics to calculate text position
        FontMetrics fm = g2d.getFontMetrics();

        // Text position
        int x = image.getWidth() - fm.stringWidth(date) - 10;
        int y = image.getHeight() - fm.getDescent() - 10;

        // Draw the text
        g2d.drawString(date, x, y);
        g2d.dispose();

        return image;
    }

    // Method to get color from JComboBox
    private Color getColorFromComboBox() {
        String selectedColor = (String) colorComboBox.getSelectedItem();
        switch (selectedColor) {
            case "Black":
                return Color.BLACK;
            case "Red":
                return Color.RED;
            case "Green":
                return Color.GREEN;
            case "Blue":
                return Color.BLUE;
            default:
                return Color.WHITE;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatePhotoENG frame = new DatePhotoENG();
            frame.setVisible(true);
        });
    }
}

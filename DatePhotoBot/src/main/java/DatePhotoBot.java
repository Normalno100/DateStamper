import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DatePhotoBot extends TelegramLongPollingBot {
    private final Map<Long, UserSession> userSessions = new HashMap<>(); // Сессии для каждого пользователя

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();

            // Обработка изображений
            if (message.hasPhoto()) {
                List<PhotoSize> photos = message.getPhoto();
                PhotoSize largestPhoto = photos.stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null);

                if (largestPhoto != null) {
                    try {
                        File photoFile = downloadPhoto(largestPhoto);
                        userSessions.put(chatId, new UserSession(photoFile));
                        sendText(chatId, "Photo received. Enter date and time (format: HH:mm dd-MM-yyyy):");
                    } catch (Exception e) {
                        sendText(chatId, "Failed to download photo.");
                    }
                }
            }
            // Обработка текстовых сообщений
            else if (message.hasText()) {
                handleTextMessage(chatId, message.getText());
            }
        }
    }

    private void handleTextMessage(Long chatId, String text) {
        UserSession session = userSessions.get(chatId);
        if (session == null) {
            sendText(chatId, "Please send a photo to start.");
            return;
        }

        if (session.getDate() == null) {
            try {
                Date date = new SimpleDateFormat("HH:mm dd-MM-yyyy").parse(text);
                session.setDate(date);
                sendText(chatId, "Enter font size (10-100):");
            } catch (Exception e) {
                sendText(chatId, "Invalid date format. Try again (format: HH:mm dd-MM-yyyy).");
            }
        }
        else if (session.getFontSize() == 0) {
            try {
                int fontSize = Integer.parseInt(text);
                if (fontSize < 10 || fontSize > 100) throw new NumberFormatException();
                session.setFontSize(fontSize);
                sendText(chatId, "Choose color: White, Black, Red, Green, Blue.");
            } catch (NumberFormatException e) {
                sendText(chatId, "Font size must be between 10 and 100.");
            }
        }
        else if (session.getColor() == null) {
            if (session.setColor(text)) {
                processImage(chatId, session);
            } else {
                sendText(chatId, "Invalid color. Choose: White, Black, Red, Green, Blue.");
            }
        }
    }

    private void processImage(Long chatId, UserSession session) {
        try {
            BufferedImage image = ImageIO.read(session.getPhoto());
            addDateToImage(image, session);
            File outputFile = new File("output.jpg");
            ImageIO.write(image, "jpg", outputFile);

            sendPhoto(chatId, outputFile);
            session.clear();
        } catch (Exception e) {
            sendText(chatId, "Failed to process image.");
        }
    }

    private void addDateToImage(BufferedImage image, UserSession session) {
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(new Font("Arial", Font.BOLD, session.getFontSize()));
        g2d.setColor(session.getColor());

        String dateText = new SimpleDateFormat("HH:mm dd-MM-yyyy").format(session.getDate());
        FontMetrics fm = g2d.getFontMetrics();
        int x = image.getWidth() - fm.stringWidth(dateText) - 10;
        int y = image.getHeight() - fm.getDescent() - 10;

        g2d.drawString(dateText, x, y);
        g2d.dispose();
    }

    private void sendText(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendPhoto(Long chatId, File photo) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(photo));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private File downloadPhoto(PhotoSize photo) throws IOException, TelegramApiException {
        // Получаем информацию о файле от Telegram API
        org.telegram.telegrambots.meta.api.objects.File fileInfo = execute(new GetFile(photo.getFileId()));
        String filePath = fileInfo.getFilePath(); // Получаем путь к файлу

        // Скачиваем файл
        File photoFile = downloadFile(filePath);

        // Копируем содержимое временного файла для дальнейшей обработки
        File tempFile = File.createTempFile("photo", ".jpg");
        try (InputStream is = new FileInputStream(photoFile);
             OutputStream os = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    @Override
    public String getBotUsername() {
        return Bot.botName;
    }

    @Override
    public String getBotToken() {return Bot.botToken; }


    private static class UserSession {
        private final File photo;
        private Date date;
        private int fontSize;
        private Color color;

        public UserSession(File photo) {
            this.photo = photo;
        }

        public File getPhoto() { return photo; }
        public Date getDate() { return date; }
        public void setDate(Date date) { this.date = date; }
        public int getFontSize() { return fontSize; }
        public void setFontSize(int fontSize) { this.fontSize = fontSize; }
        public Color getColor() { return color; }
        public boolean setColor(String colorName) {
            switch (colorName.toLowerCase()) {
                case "white": color = Color.WHITE; break;
                case "black": color = Color.BLACK; break;
                case "red": color = Color.RED; break;
                case "green": color = Color.GREEN; break;
                case "blue": color = Color.BLUE; break;
                default: return false;
            }
            return true;
        }

        public void clear() {
            date = null;
            fontSize = 0;
            color = null;
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new DatePhotoBot());
            System.out.println("Bot started successfully!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

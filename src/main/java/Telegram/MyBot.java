package Telegram;

import Config.ConfigurationManager;
import DEBUG.Debug;
import Hash.HashConnectors;
import Hash.HashGenerator;
import Localization.LocManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyBot extends TelegramLongPollingBot {
    private static int totalMessagesProcessed = 0;
    private static int totalSecretChatsCreated = 0;
    public int getTotalMessagesProcessed() { return totalMessagesProcessed; }
    public int getTotalSecretChatsCreated() {
        return totalSecretChatsCreated;
    }
    public void setTotalMessagesProcessed(int num) {
        totalMessagesProcessed = num;
    }
    public void setTotalSecretChatsCreated(int num) {
        totalSecretChatsCreated = num;
    }
    public static void plusOneTotalSecretChatsCreated() {
        totalSecretChatsCreated++;
    }
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            Debug.functionDebug("update received. the text is: " + update.getMessage().getText());
            UpdateHandler updateHandler = new UpdateHandler(update.getMessage().getText(), update.getMessage().getChatId());
            totalMessagesProcessed++;

            LocManager manager = new LocManager();
            String language = manager.getUserLanguage(update.getMessage().getChatId());
            if (language != null) {
                if (updateHandler.isCommandNewHash()) {
                    updateHandler.handleCommandNewHash(language);
                } else if (HashGenerator.isSHA256Hash(update.getMessage().getText())) {
                    updateHandler.handleMessageWhichIsHash();
                } else if (updateHandler.isCommandStop()) {
                    updateHandler.handleCommandStop();
                } else if (updateHandler.isCommandHelp()) {
                    updateHandler.handleCommandHelp(language);
                } else if (updateHandler.isCommandSetLanguage()) {
                    updateHandler.handleCommandSetLanguage();
                } else if (updateHandler.isPromoCode()) {
                    updateHandler.handlePromoCode();
                } else if (updateHandler.isThisChatIdSavedAsHost()) {
                    updateHandler.handleChatIdSavedAsHost();
                } else if (updateHandler.isThisChatIdSavedAsPair()) {
                    updateHandler.handleChatIdSavedAsPair();
                } else {
                    updateHandler.unknownMessage(language);
                }
            }
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            long chatId = callbackQuery.getMessage().getChatId();
            if ("ru".equals(data) || "en".equals(data) || "pl".equals(data) || "ar".equals(data) || "de".equals(data)) {
                LocManager locManager = new LocManager();
                locManager.setChatLanguage(chatId, data);
            }
        }
    }
    public void sendMessageToUser(String chatID, String text) throws IOException {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(text);
        message.setParseMode(ParseMode.HTML);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendLanguageMenu(long chatId) {
        SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Choose a language:");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        InlineKeyboardButton plButton = new InlineKeyboardButton();
        plButton.setText("Polski");
        plButton.setCallbackData("pl");
        row1.add(plButton);

        InlineKeyboardButton enButton = new InlineKeyboardButton();
        enButton.setText("English");
        enButton.setCallbackData("en");
        row1.add(enButton);

        InlineKeyboardButton arButton = new InlineKeyboardButton();
        arButton.setText("عربي");
        arButton.setCallbackData("ar");
        row1.add(arButton);

        InlineKeyboardButton ruButton = new InlineKeyboardButton();
        ruButton.setText("Русский");
        ruButton.setCallbackData("ru");
        row2.add(ruButton);

        InlineKeyboardButton deButton = new InlineKeyboardButton();
        deButton.setText("Deutsch");
        deButton.setCallbackData("de");
        row2.add(deButton);

        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getBotUsername() {
        return "NoNames";
    }
    @Override
    public String getBotToken() {
        ConfigurationManager config = new ConfigurationManager();
        if(Debug.isDevBuild){
            return config.getConfigValue("bot_token_dev");
        }
        return config.getConfigValue("bot_token");
    }
    private final Object updateLock = new Object();
    private int maxUpdatesPerFetch = 100;
    private long lastUpdateId = 0;
    public void startFetchingUpdates() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    GetUpdates getUpdates = new GetUpdates();
                        getUpdates.setOffset((int) (lastUpdateId + 1));
                    List<Update> updates = execute(getUpdates);

                    int updatesProcessed = 0;
                    for (Update update : updates) {
                        synchronized (updateLock) {
                            if (update.getUpdateId() > lastUpdateId) {
                                onUpdateReceived(update);
                                lastUpdateId = update.getUpdateId();
                            }
                        }
                        updatesProcessed++;
                        if (updatesProcessed >= maxUpdatesPerFetch) {
                            break;
                        }
                    }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 300);
    }
    static class Cleaner implements Runnable {
        public void run() {
            HashConnectors connectors = new HashConnectors();
            try {
                connectors.clearConnections(true);
            } catch (IOException e) {
                Debug debug = new Debug();
                try {
                    debug.errorMessageDebug(e.getMessage());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Debug.isDevBuild = true;
            Debug.PROJECT_PATH = "C:/Users/kotmi/Documents/GitHub/NoNames/src/main/";
            System.out.println("Dev environment is set up");
        }else {
            Debug.isDevBuild = false;
            Debug.PROJECT_PATH = "/root/build/";
        }
        Debug debug = new Debug();
        debug.cleanLogs();

        System.out.println("Bot Started !!!");

        MyBot myBot = new MyBot();
        Debug.functionDebug(myBot.getBotToken());
        myBot.startFetchingUpdates();

        StatsReporter reporter = new StatsReporter(myBot);
        reporter.loadCounters();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable cleanerTask = new Cleaner();
        scheduler.scheduleAtFixedRate(cleanerTask, 0, 1, TimeUnit.HOURS);

        ScheduledExecutorService statsScheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable statsCounter = new StatsReporter(myBot);
        statsScheduler.scheduleAtFixedRate(statsCounter, 1, 10, TimeUnit.MINUTES);
    }
}
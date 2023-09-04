package Telegram;

import DEBUG.Debug;

import java.io.*;
import java.util.Properties;

public class StatsReporter implements Runnable {
    private MyBot bot;
    private final String reportFilePath = Debug.PROJECT_PATH + "resources/Report.properties";

    public StatsReporter(MyBot bot) {
        this.bot = bot;
    }

    public void loadCounters() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(reportFilePath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Failed to load Report.properties: " + e.getMessage());
        }
        bot.setTotalMessagesProcessed(Integer.parseInt(properties.getProperty("totalMessagesProcessed")));
        bot.setTotalSecretChatsCreated(Integer.parseInt(properties.getProperty("totalSecretChatsCreated")));
    }

    @Override
    public void run() {
        if(!Debug.isDevBuild) {
            Properties properties = new Properties();
            try (InputStream inputStream = new FileInputStream(reportFilePath)) {
                properties.load(inputStream);
            } catch (IOException e) {
                System.err.println("Failed to load Report.properties: " + e.getMessage());
            }
            //a
            Debug.functionDebug("Stat triggered woke up");
            Debug.functionDebug(properties.getProperty("totalMessagesProcessed"));
            Debug.functionDebug(String.valueOf(bot.getTotalMessagesProcessed()));
            if (!properties.getProperty("totalMessagesProcessed").equals(String.valueOf(bot.getTotalMessagesProcessed()))) {
                Debug.functionDebug("Stat sender woke up");
                properties.setProperty("totalMessagesProcessed", String.valueOf(bot.getTotalMessagesProcessed()));
                properties.setProperty("totalSecretChatsCreated", String.valueOf(bot.getTotalSecretChatsCreated()));
                try (OutputStream outputStream = new FileOutputStream(reportFilePath)) {
                    properties.store(outputStream, "Bot Statistics");
                    bot.sendMessageToUser("-1001809745382", "Total Messages number: " + bot.getTotalMessagesProcessed() +
                            "\n" + "Total Secret Chats created number: " + bot.getTotalSecretChatsCreated());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Failed to update report: " + e.getMessage());
                }
            }
        }
    }
}

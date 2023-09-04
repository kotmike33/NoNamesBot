package Hash;

import Config.ConfigurationManager;
import DEBUG.Debug;
import Localization.LocManager;
import Telegram.MyBot;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.Set;

public class HashConnectors {
    private final Properties properties = new Properties();
    private final String connectorsFile = Debug.PROJECT_PATH + "resources/connectors.properties";

    public HashConnectors() {
        loadConfig();
    }
    private void loadConfig() {
        try (InputStream inputStream = new FileInputStream(connectorsFile)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveConfig() {
        try (OutputStream outputStream = new FileOutputStream(connectorsFile)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getConfigValue(String key) {
        return properties.getProperty(key);
    }
    public String getKeyByValue(String value) {
        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            if (properties.getProperty(key).equals(value)) {
                return key;
            }
        }
        return null;
    }
    public void deletePair(String value, boolean isAutomated) throws IOException {
        LocManager locManager = new LocManager();
        if(!properties.containsKey(value) && !properties.containsValue(value)){
            MyBot bot = new MyBot();
            if(!isAutomated) {
                bot.sendMessageToUser(value, locManager.getTHERE_IS_NOTHING_TO_DELETE(locManager.getUserLanguage(Long.valueOf(value))));
            }
        }else {
            int i = 0;
            while (properties.containsKey(value) || properties.containsValue(value)) {
                i++;
                if (i > 4) {
                    break;
                }
                try {
                    deleteData(value);
                    if (!value.contains("Time")) {
                        deleteData(value + "Time");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void deleteData(String value) {
        LocManager locManager = new LocManager();
        MyBot bot = new MyBot();
        HashConnectors connectors = new HashConnectors();

        if (properties.containsKey(value)) {
            Debug.functionDebug("deletePair   KEY: " + value);
            Debug.functionDebug("deletePair   VALUE: " + connectors.getConfigValue(value));

            if (!value.contains("Time")) {
                try {
                    String configValue = connectors.getConfigValue(value);
                    if (HashGenerator.isSHA256Hash(configValue)) {
                        bot.sendMessageToUser(value, locManager.getYOUR_HASH_IS_OUTDATED(locManager.getUserLanguage(Long.valueOf(value))));
                    } else {
                        bot.sendMessageToUser(connectors.getConfigValue(value), locManager.getTHE_LAST_HAS_BEEN_STOPPED(locManager.getUserLanguage(Long.valueOf(connectors.getConfigValue(value)))));
                        bot.sendMessageToUser(value, locManager.getTHE_LAST_HAS_BEEN_STOPPED(locManager.getUserLanguage(Long.valueOf(value))));
                    }
                } catch (Exception e) {
                    Debug.functionDebug("Failed to send /stop reply. chat is: " + value);
                    e.printStackTrace();
                }
            }
            properties.remove(value);
            saveConfig();
        } else if (properties.containsValue(value)) {
            Debug.functionDebug("deletePair   VALUE: " + value);
            Debug.functionDebug("deletePair   KEY: " + connectors.getKeyByValue(value));
            try {
                String keyByValue = connectors.getKeyByValue(value);

                if (HashGenerator.isSHA256Hash(value)) {
                    bot.sendMessageToUser(keyByValue, locManager.getYOUR_HASH_IS_OUTDATED(locManager.getUserLanguage(Long.valueOf(keyByValue))));
                } else {
                    bot.sendMessageToUser(keyByValue, locManager.getTHE_LAST_HAS_BEEN_STOPPED(locManager.getUserLanguage(Long.valueOf(keyByValue))));
                    bot.sendMessageToUser(value, locManager.getTHE_LAST_HAS_BEEN_STOPPED(locManager.getUserLanguage(Long.valueOf(value))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String key = connectors.getKeyByValue(value);
            properties.remove(key);
            saveConfig();
            deleteData(key);
        }
    }
    public boolean isPaired(String chatID) {
        if(properties.containsKey(chatID) && !HashGenerator.isSHA256Hash(getConfigValue(chatID))){
            return true;
        }
        if(properties.containsValue(chatID) && !HashGenerator.isSHA256Hash(getKeyByValue(chatID))){
            return true;
        }
        return false;
    }
    public boolean isPairedWithHash(String chatID){
        if(HashGenerator.isSHA256Hash(properties.getProperty(chatID))){
            return true;
        }
        return false;
    }
    public void setConfigValue(String key, String value) {
        properties.setProperty(key, value);
        String time = LocalTime.now() + "_" + LocalDate.now();
        properties.setProperty(key+"Time",time);
        saveConfig();
    }
    public void clearConnections(boolean isAutomated) throws IOException {
        loadConfig();
        Debug.functionDebug("clearConnections() STARTED");
        Set<String> keys = properties.stringPropertyNames();

        for (String key : keys) {
            if (key.contains("Time")) {
                String timeAndDate = getConfigValue(key);
                String[] parts = timeAndDate.split("_");
                String date = parts[1];

                LocalDate connectionDate = LocalDate.parse(date);
                LocalDate localDate = LocalDate.now();

                if (ChronoUnit.DAYS.between(connectionDate, localDate) >= 5) {
                    try {
                        Debug.functionDebug("ChronoUnit.DAYS.between(localDate,connectionDate)>1 is true  ********************");
                        deletePair(key.replaceFirst("Time", ""),isAutomated);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        saveConfig();
    }
}
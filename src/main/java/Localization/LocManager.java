package Localization;

import DEBUG.Debug;
import Telegram.MyBot;

import java.io.*;
import java.util.Properties;

public class LocManager{
    private final Properties properties = new Properties();
    private final String locFile = Debug.PROJECT_PATH + "resources/loc.properties";
    private String chatLanguage;
    public LocManager(){
        loadConfig();
    }
    public String getUserLanguage(Long chatID){
        if(properties.containsKey(String.valueOf(chatID))){
            this.chatLanguage = properties.getProperty(String.valueOf(chatID));
            Debug.functionDebug("chat: " + chatID + " Lang: " + chatLanguage);
            return this.chatLanguage;
        }else {
            Debug.functionDebug("chat: " + chatID + " Lang is not found in loc.properties");
            MyBot bot = new MyBot();
            bot.sendLanguageMenu(chatID);
        }
        return null;
    }
    private void loadConfig() {
        try (InputStream inputStream = new FileInputStream(locFile)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveConfig() {
        try (OutputStream outputStream = new FileOutputStream(locFile)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setChatLanguage(Long chatID, String chatLanguage) {
        MyBot bot = new MyBot();
        String welcomeLocMessage;
        properties.setProperty(String.valueOf(chatID), chatLanguage);
        saveConfig();

        switch (chatLanguage) {
            case "ru":
                welcomeLocMessage = "Привет! Ты выбрал Русский";
                break;
            case "en":
                welcomeLocMessage = "Hey! You selected English";
                break;
            case "pl":
                welcomeLocMessage = "Cześć! Wybrałeś Polski";
                break;
            case "ar":
                welcomeLocMessage = "مرحبًا! لقد اخترت اللغة العربية";
                break;
            case "de":
                welcomeLocMessage = "Hallo! Du hast Deutsch ausgewählt";
                break;
            default:
                welcomeLocMessage = "Hello! You selected an unsupported language";
        }
        try {
            bot.sendMessageToUser(String.valueOf(chatID), welcomeLocMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getYOU_HAVE_ALREADY_CREATED_HASH(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.YOU_HAVE_ALREADY_CREATED_HASH;
            case "pl":
                return LocPl.YOU_HAVE_ALREADY_CREATED_HASH;
            case "ar":
                return LocAr.YOU_HAVE_ALREADY_CREATED_HASH;
            case "de":
                return LocDe.YOU_HAVE_ALREADY_CREATED_HASH;
            default:
                return LocEn.YOU_HAVE_ALREADY_CREATED_HASH;
        }
    }
    public String getNOW_YOU_CAN_SHARE_THIS_HASH(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.NOW_YOU_CAN_SHARE_THIS_HASH;
            case "pl":
                return LocPl.NOW_YOU_CAN_SHARE_THIS_HASH;
            case "ar":
                return LocAr.NOW_YOU_CAN_SHARE_THIS_HASH;
            case "de":
                return LocDe.NOW_YOU_CAN_SHARE_THIS_HASH;
            default:
                return LocEn.NOW_YOU_CAN_SHARE_THIS_HASH;
        }
    }
    public String getSTOP_YOUR_CURRENT_CHAT_TO_CREATE(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.STOP_YOUR_CURRENT_CHAT_TO_CREATE;
            case "pl":
                return LocPl.STOP_YOUR_CURRENT_CHAT_TO_CREATE;
            case "ar":
                return LocAr.STOP_YOUR_CURRENT_CHAT_TO_CREATE;
            case "de":
                return LocDe.STOP_YOUR_CURRENT_CHAT_TO_CREATE;
            default:
                return LocEn.STOP_YOUR_CURRENT_CHAT_TO_CREATE;
        }
    }

    public String getSTOP_YOUR_CURRENT_CHAT_TO_CONNECT(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.STOP_YOUR_CURRENT_CHAT_TO_CONNECT;
            case "pl":
                return LocPl.STOP_YOUR_CURRENT_CHAT_TO_CONNECT;
            case "ar":
                return LocAr.STOP_YOUR_CURRENT_CHAT_TO_CONNECT;
            case "de":
                return LocDe.STOP_YOUR_CURRENT_CHAT_TO_CONNECT;
            default:
                return LocEn.STOP_YOUR_CURRENT_CHAT_TO_CONNECT;
        }
    }

    public String getYOU_CANNOT_START_WITH_YOURSELF(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.YOU_CANNOT_START_WITH_YOURSELF;
            case "pl":
                return LocPl.YOU_CANNOT_START_WITH_YOURSELF;
            case "ar":
                return LocAr.YOU_CANNOT_START_WITH_YOURSELF;
            case "de":
                return LocDe.YOU_CANNOT_START_WITH_YOURSELF;
            default:
                return LocEn.YOU_CANNOT_START_WITH_YOURSELF;
        }
    }
    public String getHASH_OWNER_ALREADY_ANOTHER_CHAT(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.HASH_OWNER_ALREADY_ANOTHER_CHAT;
            case "pl":
                return LocPl.HASH_OWNER_ALREADY_ANOTHER_CHAT;
            case "ar":
                return LocAr.HASH_OWNER_ALREADY_ANOTHER_CHAT;
            case "de":
                return LocDe.HASH_OWNER_ALREADY_ANOTHER_CHAT;
            default:
                return LocEn.HASH_OWNER_ALREADY_ANOTHER_CHAT;
        }
    }
    public String getSECRET_CHAT_STARTED(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.SECRET_CHAT_STARTED;
            case "pl":
                return LocPl.SECRET_CHAT_STARTED;
            case "ar":
                return LocAr.SECRET_CHAT_STARTED;
            case "de":
                return LocDe.SECRET_CHAT_STARTED;
            default:
                return LocEn.SECRET_CHAT_STARTED;
        }
    }
    public String getHASH_IS_NOT_ACTIVE(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.HASH_IS_NOT_ACTIVE;
            case "pl":
                return LocPl.HASH_IS_NOT_ACTIVE;
            case "ar":
                return LocAr.HASH_IS_NOT_ACTIVE;
            case "de":
                return LocDe.HASH_IS_NOT_ACTIVE;
            default:
                return LocEn.HASH_IS_NOT_ACTIVE;
        }
    }
    public String getHELP_MESSAGE(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.HELP_MESSAGE;
            case "pl":
                return LocPl.HELP_MESSAGE;
            case "ar":
                return LocAr.HELP_MESSAGE;
            case "de":
                return LocDe.HELP_MESSAGE;
            default:
                return LocEn.HELP_MESSAGE;
        }
    }
    public String getCURRENTLY_YOU_ARE_NOT_IN_CHAT(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.CURRENTLY_YOU_ARE_NOT_IN_CHAT;
            case "pl":
                return LocPl.CURRENTLY_YOU_ARE_NOT_IN_CHAT;
            case "ar":
                return LocAr.CURRENTLY_YOU_ARE_NOT_IN_CHAT;
            case "de":
                return LocDe.CURRENTLY_YOU_ARE_NOT_IN_CHAT;
            default:
                return LocEn.CURRENTLY_YOU_ARE_NOT_IN_CHAT;
        }
    }
    public String getTHERE_IS_NOTHING_TO_DELETE(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.THERE_IS_NOTHING_TO_DELETE;
            case "pl":
                return LocPl.THERE_IS_NOTHING_TO_DELETE;
            case "ar":
                return LocAr.THERE_IS_NOTHING_TO_DELETE;
            case "de":
                return LocDe.THERE_IS_NOTHING_TO_DELETE;
            default:
                return LocEn.THERE_IS_NOTHING_TO_DELETE;
        }
    }
    public String getYOUR_HASH_IS_OUTDATED(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.YOUR_HASH_IS_OUTDATED;
            case "pl":
                return LocPl.YOUR_HASH_IS_OUTDATED;
            case "ar":
                return LocAr.YOUR_HASH_IS_OUTDATED;
            case "de":
                return LocDe.YOUR_HASH_IS_OUTDATED;
            default:
                return LocEn.YOUR_HASH_IS_OUTDATED;
        }
    }
    public String getTHE_LAST_HAS_BEEN_STOPPED(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.THE_LAST_HAS_BEEN_STOPPED;
            case "pl":
                return LocPl.THE_LAST_HAS_BEEN_STOPPED;
            case "ar":
                return LocAr.THE_LAST_HAS_BEEN_STOPPED;
            case "de":
                return LocDe.THE_LAST_HAS_BEEN_STOPPED;
            default:
                return LocEn.THE_LAST_HAS_BEEN_STOPPED;
        }
    }
    public String getPROMO_CODE_ACTIVATED(String chatLanguage) {
        switch (chatLanguage) {
            case "ru":
                return LocRu.PROMO_CODE_ACTIVATED;
            case "pl":
                return LocPl.PROMO_CODE_ACTIVATED;
            case "ar":
                return LocAr.PROMO_CODE_ACTIVATED;
            case "de":
                return LocDe.PROMO_CODE_ACTIVATED;
            default:
                return LocEn.PROMO_CODE_ACTIVATED;
        }
    }
}

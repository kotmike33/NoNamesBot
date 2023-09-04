package Telegram;

import DEBUG.Debug;
import Hash.HashConnectors;
import Hash.HashGenerator;
import Localization.LocManager;

import java.io.IOException;

public class UpdateHandler extends MyBot{
    private String messageText;
    private String chatID;
    public UpdateHandler(String text, Long ID) {
        this.messageText = text;
        this.chatID = String.valueOf(ID);
    }
    public boolean isCommandNewHash (){
        if(messageText.contains("/new_hash")){
            return true;
        }
        return false;
    }
    public void handleCommandNewHash(String language){
        LocManager locManager = new LocManager();
        locManager.getUserLanguage(Long.valueOf(chatID));
        Debug debug = new Debug();
        HashConnectors connectors = new HashConnectors();
        if(!connectors.isPaired(chatID)){
            if(connectors.isPairedWithHash(chatID)){
                try{
                    sendMessageToUser(chatID,locManager.getYOU_HAVE_ALREADY_CREATED_HASH(language));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                try {
                    sendMessageToUser(
                            chatID,"<code>" + HashGenerator.createHash(Long.valueOf(chatID)) + "</code>\n\n\n" +
                                    locManager.getNOW_YOU_CAN_SHARE_THIS_HASH(language)
                    );
                } catch (IOException e) {
                    try {
                        debug.errorMessageDebug(e.getMessage());
                    } catch (IOException ex) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            try {
                sendMessageToUser(
                        chatID,
                        locManager.getSTOP_YOUR_CURRENT_CHAT_TO_CREATE(language)
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void handleMessageWhichIsHash() {
        HashConnectors connectors = new HashConnectors();
        LocManager locManager = new LocManager();
        locManager.getUserLanguage(Long.valueOf(chatID));
        if(connectors.isPaired(chatID)){
            try {
                sendMessageToUser(chatID,locManager.getSTOP_YOUR_CURRENT_CHAT_TO_CONNECT(locManager.getUserLanguage(Long.valueOf(chatID))));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(connectors.getKeyByValue(messageText)!=null){
            String hostChatID = connectors.getKeyByValue(messageText);
            if(hostChatID.equals(chatID)){
                try{
                    sendMessageToUser(chatID,locManager.getYOU_CANNOT_START_WITH_YOURSELF(locManager.getUserLanguage(Long.valueOf(chatID))));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                if (connectors.isPaired(hostChatID)) {
                    try {
                        sendMessageToUser(chatID, locManager.getHASH_OWNER_ALREADY_ANOTHER_CHAT(locManager.getUserLanguage(Long.valueOf(chatID))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    connectors.setConfigValue(hostChatID, chatID);
                    connectors.saveConfig();
                    try {
                        sendMessageToUser(hostChatID, locManager.getSECRET_CHAT_STARTED(locManager.getUserLanguage(Long.valueOf(hostChatID))));
                        sendMessageToUser(chatID, locManager.getSECRET_CHAT_STARTED(locManager.getUserLanguage(Long.valueOf(chatID))));
                        MyBot.plusOneTotalSecretChatsCreated();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }else {
            try {
                sendMessageToUser(chatID, locManager.getHASH_IS_NOT_ACTIVE(locManager.getUserLanguage(Long.valueOf(chatID))));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public boolean isCommandStop (){
        if(messageText.contains("/stop")){
            return true;
        }
        return false;
    }
    public void handleCommandStop(){
        HashConnectors connectors = new HashConnectors();
        try {
            connectors.deletePair(chatID, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isCommandHelp(){
        if(messageText.contains("/help")){
            return true;
        }
        return false;
    }
    public void handleCommandHelp(String language) {
        LocManager locManager = new LocManager();
        locManager.getUserLanguage(Long.valueOf(chatID));
        try {
            MyBot bot = new MyBot();
            bot.sendMessageToUser(chatID, locManager.getHELP_MESSAGE(language));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean isCommandSetLanguage(){
        if(messageText.equals("/set_language")){
            return true;
        }
        return false;
    }
    public void handleCommandSetLanguage(){
        MyBot bot = new MyBot();
        bot.sendLanguageMenu(Long.parseLong(chatID));
    }
    public boolean isPromoCode(){
        if (messageText.equals("987xYz:ghI:654") ||
                messageText.equals("543Aaa:BCd:222") ||
                messageText.equals("654abc:def:321") ||
                messageText.equals("246mno:pqr:135")
        ) {
            return true;
        }
        return false;
    }
    public void handlePromoCode(){
        LocManager locManager = new LocManager();
        try {
            sendMessageToUser(chatID, locManager.getPROMO_CODE_ACTIVATED(locManager.getUserLanguage(Long.valueOf(chatID))));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean isThisChatIdSavedAsHost(){
        HashConnectors connectors = new HashConnectors();
        if(connectors.getConfigValue(chatID)!=null && !HashGenerator.isSHA256Hash(connectors.getConfigValue(chatID))){
            return true;
        }
        return false;
    }
    public void handleChatIdSavedAsHost() {
        HashConnectors connectors = new HashConnectors();
        String chatIdPairedValue = connectors.getConfigValue(chatID);
        try {
            sendMessageToUser(chatIdPairedValue, messageText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isThisChatIdSavedAsPair(){
        HashConnectors connectors = new HashConnectors();
        if(connectors.getKeyByValue(chatID)!=null && !HashGenerator.isSHA256Hash(connectors.getKeyByValue(chatID))){
            return true;
        }
        return false;
    }
    public void handleChatIdSavedAsPair(){
        HashConnectors connectors = new HashConnectors();
        String hostChatId = connectors.getKeyByValue(chatID);
        try {
            sendMessageToUser(hostChatId, messageText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void unknownMessage(String language){
        try {
            LocManager locManager = new LocManager();
            locManager.getUserLanguage(Long.valueOf(chatID));
            sendMessageToUser(chatID,locManager.getCURRENTLY_YOU_ARE_NOT_IN_CHAT(language));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package Hash;

import DEBUG.Debug;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashGenerator {
    public static String createHash(Long chatId) throws IOException {
        HashConnectors connectors = new HashConnectors();
        HashGenerator hashGenerator = new HashGenerator();
        Debug debug = new Debug();
        debug.functionDebug(hashGenerator.timeFormatter());
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest((chatId + hashGenerator.timeFormatter()).getBytes(StandardCharsets.UTF_8));
            String result = bytesToHex(hashBytes);

            connectors.setConfigValue(String.valueOf(chatId),result);
            connectors.saveConfig();

            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public String timeFormatter() {
        String time = LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", "");
        String date = LocalDate.now().toString().replaceAll("-","");
        return time + date;
    }
    public static boolean isSHA256Hash(String input) {
        if(input!=null && input.length()>1) {
            String sha256Pattern = "^[0-9a-fA-F]{64}$";
            Pattern pattern = Pattern.compile(sha256Pattern);
            Matcher matcher = pattern.matcher(input);
            return matcher.matches();
        }else {
            return false;
        }
    }
}

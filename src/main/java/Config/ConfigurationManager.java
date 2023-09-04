package Config;

import DEBUG.Debug;

import java.io.*;
import java.util.Properties;

public class ConfigurationManager {
	public Properties properties;
	private final String configFile = Debug.PROJECT_PATH + "resources/config.properties";

	public ConfigurationManager() {
		properties = new Properties();
		loadConfig();
	}

	private void loadConfig() {
		try (InputStream inputStream = new FileInputStream(configFile)) {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveConfig() {
		try (OutputStream outputStream = new FileOutputStream(configFile)) {
			properties.store(outputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getConfigValue(String key) {
		return properties.getProperty(key);
	}

	public void setConfigValue(String key, String value) {
		properties.setProperty(key, value);
	}

	public static void main(String[] args) {
		Debug debug = new Debug();
		ConfigurationManager configManager = new ConfigurationManager();
		String value = configManager.getConfigValue("non existing value");
		System.out.println("Value: " + value);
	}
}
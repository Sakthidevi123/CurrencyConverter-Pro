package currencyconverter;

import java.io.*;
import java.util.Properties;

/**
 * Persists user preferences to {@code settings.properties} in the working
 * directory. The file is created automatically on first run.
 */
public final class SettingsManager {

    public static final String KEY_THEME           = "theme";
    public static final String KEY_AUTOSAVE        = "autoSaveHistory";
    public static final String KEY_ANIMATIONS      = "animations";
    public static final String KEY_SOUND           = "soundEffects";
    public static final String KEY_FONT_SIZE       = "fontSize";
    public static final String KEY_DEFAULT_FROM    = "defaultFrom";
    public static final String KEY_DEFAULT_TO      = "defaultTo";
    public static final String KEY_LANGUAGE        = "language";
    public static final String KEY_LAST_AMOUNT     = "lastAmount";
    public static final String KEY_DAILY_DATE      = "dailyDate";
    public static final String KEY_DAILY_COUNT     = "dailyCount";
    public static final String KEY_TOTAL_COUNT     = "totalCount";

    private static final File FILE = new File("settings.properties");

    private final Properties properties = new Properties();

    public SettingsManager() {
        load();
    }

    private void load() {
        applyDefaults();
        if (FILE.exists()) {
            try (InputStream in = new FileInputStream(FILE)) {
                properties.load(in);
            } catch (IOException ex) {
                /* Corrupt or unreadable settings fall back to the defaults. */
            }
        } else {
            save();
        }
    }

    private void applyDefaults() {
        properties.setProperty(KEY_THEME, "Light");
        properties.setProperty(KEY_AUTOSAVE, "true");
        properties.setProperty(KEY_ANIMATIONS, "true");
        properties.setProperty(KEY_SOUND, "false");
        properties.setProperty(KEY_FONT_SIZE, "Medium");
        properties.setProperty(KEY_DEFAULT_FROM, "USD");
        properties.setProperty(KEY_DEFAULT_TO, "INR");
        properties.setProperty(KEY_LANGUAGE, "English");
        properties.setProperty(KEY_LAST_AMOUNT, "");
        properties.setProperty(KEY_DAILY_DATE, "");
        properties.setProperty(KEY_DAILY_COUNT, "0");
        properties.setProperty(KEY_TOTAL_COUNT, "0");
    }

    public String get(String key)                 { return properties.getProperty(key, ""); }
    public String get(String key, String def)     { return properties.getProperty(key, def); }
    public boolean getBoolean(String key)         { return Boolean.parseBoolean(properties.getProperty(key, "false")); }

    public int getInt(String key, int def) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(def)));
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    public void set(String key, String value) {
        properties.setProperty(key, value == null ? "" : value);
    }

    public void set(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public void save() {
        try (OutputStream out = new FileOutputStream(FILE)) {
            properties.store(out, "Currency Converter Pro - user settings");
        } catch (IOException ex) {
            /* Settings are non critical: never interrupt the user. */
        }
    }
}

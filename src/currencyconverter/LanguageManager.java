package currencyconverter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Very small in-memory translation table. Keeping it in code avoids shipping
 * resource bundles while still supporting the six advertised languages.
 */
public final class LanguageManager {

    public static final String[] LANGUAGES = {"English", "Tamil", "Hindi", "French", "German", "Japanese"};

    private static final Map<String, Map<String, String>> TABLE = new LinkedHashMap<>();

    static {
        TABLE.put("English", of("convert", "Convert", "clear", "Clear", "amount", "Amount",
                "from", "From Currency", "to", "To Currency", "result", "Converted Amount",
                "history", "Recent Conversions", "favorites", "Favourites", "ready", "Ready"));
        TABLE.put("Tamil", of("convert", "\u0BAE\u0BBE\u0BB1\u0BCD\u0BB1\u0BC1", "clear", "\u0B85\u0BB4\u0BBF",
                "amount", "\u0BA4\u0BCA\u0B95\u0BC8", "from", "\u0B87\u0BB0\u0BC1\u0BA8\u0BCD\u0BA4\u0BC1",
                "to", "\u0B95\u0BCD\u0B95\u0BC1", "result", "\u0BAE\u0BBE\u0BB1\u0BCD\u0BB1\u0BAA\u0BCD\u0BAA\u0B9F\u0BCD\u0B9F \u0BA4\u0BCA\u0B95\u0BC8",
                "history", "\u0B9A\u0BAE\u0BC0\u0BAA\u0BA4\u0BCD\u0BA4\u0BBF\u0BAF\u0BB5\u0BC8", "favorites", "\u0BAA\u0BBF\u0B9F\u0BBF\u0BA4\u0BCD\u0BA4\u0BB5\u0BC8",
                "ready", "\u0BA4\u0BAF\u0BBE\u0BB0\u0BCD"));
        TABLE.put("Hindi", of("convert", "\u092C\u0926\u0932\u0947\u0902", "clear", "\u0938\u093E\u092B\u093C",
                "amount", "\u0930\u093E\u0936\u093F", "from", "\u0938\u0947", "to", "\u0924\u0915",
                "result", "\u092A\u0930\u093F\u0935\u0930\u094D\u0924\u093F\u0924 \u0930\u093E\u0936\u093F",
                "history", "\u0939\u093E\u0932\u093F\u092F\u093E", "favorites", "\u092A\u0938\u0902\u0926\u0940\u0926\u093E",
                "ready", "\u0924\u0948\u092F\u093E\u0930"));
        TABLE.put("French", of("convert", "Convertir", "clear", "Effacer", "amount", "Montant",
                "from", "Devise source", "to", "Devise cible", "result", "Montant converti",
                "history", "Conversions r\u00E9centes", "favorites", "Favoris", "ready", "Pr\u00EAt"));
        TABLE.put("German", of("convert", "Umrechnen", "clear", "L\u00F6schen", "amount", "Betrag",
                "from", "Von W\u00E4hrung", "to", "Zu W\u00E4hrung", "result", "Umgerechneter Betrag",
                "history", "Letzte Umrechnungen", "favorites", "Favoriten", "ready", "Bereit"));
        TABLE.put("Japanese", of("convert", "\u5909\u63DB", "clear", "\u30AF\u30EA\u30A2", "amount", "\u91D1\u984D",
                "from", "\u5143\u306E\u901A\u8CA8", "to", "\u5148\u306E\u901A\u8CA8", "result", "\u63DB\u7B97\u91D1\u984D",
                "history", "\u5C65\u6B74", "favorites", "\u304A\u6C17\u306B\u5165\u308A", "ready", "\u6E96\u5099\u5B8C\u4E86"));
    }

    private String language = "English";

    private static Map<String, String> of(String... pairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }

    public void setLanguage(String language) {
        if (TABLE.containsKey(language)) this.language = language;
    }

    public String getLanguage() { return language; }

    /** Returns the translation, falling back to English then to the key. */
    public String t(String key) {
        Map<String, String> map = TABLE.get(language);
        if (map != null && map.containsKey(key)) return map.get(key);
        return TABLE.get("English").getOrDefault(key, key);
    }
}

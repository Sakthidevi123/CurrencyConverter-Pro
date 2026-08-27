package currencyconverter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public final class CurrencyAPI {

    private static final String API_KEY = "";

    private static final String KEYED_ENDPOINT = "https://v6.exchangerate-api.com/v6/%s/latest/%s";
    private static final String FREE_ENDPOINT  = "https://open.er-api.com/v6/latest/%s";

    private static final File CACHE_FILE = new File("rates.cache");
    private static final int TIMEOUT_MS = 8000;

    /** Result of a rate lookup, including whether it came from the offline cache. */
    public static final class RateResult {
        private final String base;
        private final Map<String, Double> rates;
        private final LocalDateTime updated;
        private final boolean offline;

        public RateResult(String base, Map<String, Double> rates, LocalDateTime updated, boolean offline) {
            this.base = base;
            this.rates = rates;
            this.updated = updated;
            this.offline = offline;
        }

        public String getBase() { return base; }
        public Map<String, Double> getRates() { return rates; }
        public LocalDateTime getUpdated() { return updated; }
        public boolean isOffline() { return offline; }

        public double rateFor(String code) {
            Double value = rates.get(code);
            return value == null ? 0d : value;
        }
    }

    /** Raised when rates can be produced neither online nor from the cache. */
    public static class RateUnavailableException extends Exception {
        public RateUnavailableException(String message) { super(message); }
    }

    public boolean hasApiKey() {
        return API_KEY != null && !API_KEY.isBlank();
    }

    /**
     * Fetches the latest rates for {@code base}. On any network failure the
     * last successfully cached response for that base is returned instead.
     *
     * @throws RateUnavailableException when no data is available at all
     */
    public RateResult fetchRates(String base) throws RateUnavailableException {
        try {
            String payload = requestJson(buildUrl(base));
            JSONObject root = new JSONObject(payload);

            JSONObject ratesJson = root.optJSONObject("conversion_rates");
            if (ratesJson == null) ratesJson = root.optJSONObject("rates");
            if (ratesJson == null) throw new IOException("Unexpected response shape");

            Map<String, Double> rates = new HashMap<>();
            for (String key : ratesJson.keySet()) {
                rates.put(key, ratesJson.getDouble(key));
            }
            writeCache(base, payload);
            return new RateResult(base, rates, LocalDateTime.now(), false);
        } catch (Exception online) {
    online.printStackTrace();   

    RateResult cached = readCache(base);
    if (cached != null) return cached;

    throw new RateUnavailableException(
            "Exchange rates could not be retrieved and no offline data is available.");
}
    }

    /** Convenience helper returning a single pair rate. */
    public double rate(RateResult result, String to) {
        return result.rateFor(to);
    }

    private String buildUrl(String base) {
        return hasApiKey()
                ? String.format(KEYED_ENDPOINT, API_KEY, base)
                : String.format(FREE_ENDPOINT, base);
    }

    private String requestJson(String endpoint) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = URI.create(endpoint).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "CurrencyConverterPro/1.0");

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + status);
            }

            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            return builder.toString();
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /* -------------------- offline cache -------------------- */

    private void writeCache(String base, String payload) {
        JSONObject cache = loadCacheRoot();
        JSONObject entry = new JSONObject();
        entry.put("savedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        entry.put("payload", payload);
        cache.put(base, entry);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(CACHE_FILE), StandardCharsets.UTF_8)) {
            writer.write(cache.toString());
        } catch (IOException ex) {
            /* Caching is best effort. */
        }
    }

    private RateResult readCache(String base) {
        JSONObject cache = loadCacheRoot();
        JSONObject entry = cache.optJSONObject(base);
        if (entry == null) return null;
        try {
            JSONObject root = new JSONObject(entry.getString("payload"));
            JSONObject ratesJson = root.optJSONObject("conversion_rates");
            if (ratesJson == null) ratesJson = root.optJSONObject("rates");
            if (ratesJson == null) return null;

            Map<String, Double> rates = new HashMap<>();
            for (String key : ratesJson.keySet()) {
                rates.put(key, ratesJson.getDouble(key));
            }
            LocalDateTime savedAt = LocalDateTime.parse(entry.getString("savedAt"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return new RateResult(base, rates, savedAt, true);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private JSONObject loadCacheRoot() {
        if (!CACHE_FILE.exists()) return new JSONObject();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(CACHE_FILE), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
            return new JSONObject(builder.toString());
        } catch (Exception ex) {
            return new JSONObject();
        }
    }
}

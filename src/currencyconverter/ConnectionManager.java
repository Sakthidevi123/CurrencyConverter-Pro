package currencyconverter;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/** Detects whether the machine currently has internet connectivity. */
public final class ConnectionManager {

    private static final String PROBE_URL = "https://open.er-api.com/v6/latest/USD";
    private static final int TIMEOUT_MS = 3500;

    private volatile boolean online;

    public boolean isOnline() { return online; }

    /** Performs a blocking connectivity probe. Call from a background thread. */
    public boolean checkConnection() {
        HttpURLConnection connection = null;
        try {
            URL url = URI.create(PROBE_URL).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.connect();
            online = connection.getResponseCode() > 0;
        } catch (Exception ex) {
            online = false;
        } finally {
            if (connection != null) connection.disconnect();
        }
        return online;
    }

    public void setOnline(boolean online) { this.online = online; }
}

package currencyconverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores favourite currency pairs (for example {@code USD>INR}) in
 * {@code favorites.txt}. A default set is seeded on first run.
 */
public final class FavoriteManager {

    private static final File FILE = new File("favorites.txt");
    private static final int MAX_FAVORITES = 12;

    private final List<String> pairs = new ArrayList<>();

    public FavoriteManager() {
        load();
    }

    public List<String> getPairs() {
        return Collections.unmodifiableList(pairs);
    }

    public static String key(String from, String to) {
        return from + ">" + to;
    }

    public boolean contains(String from, String to) {
        return pairs.contains(key(from, to));
    }

    /** Adds the pair when absent; returns true when something was added. */
    public boolean add(String from, String to) {
        String key = key(from, to);
        if (pairs.contains(key) || pairs.size() >= MAX_FAVORITES) return false;
        pairs.add(key);
        save();
        return true;
    }

    public boolean remove(String from, String to) {
        boolean removed = pairs.remove(key(from, to));
        if (removed) save();
        return removed;
    }

    /** Adds or removes the pair; returns true when it is now a favourite. */
    public boolean toggle(String from, String to) {
        if (contains(from, to)) {
            remove(from, to);
            return false;
        }
        add(from, to);
        return true;
    }

    private void load() {
        if (!FILE.exists()) {
            pairs.add("USD>INR");
            pairs.add("EUR>INR");
            pairs.add("AED>INR");
            pairs.add("GBP>USD");
            save();
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.matches("[A-Z]{3}>[A-Z]{3}") && !pairs.contains(trimmed)) {
                    pairs.add(trimmed);
                }
            }
        } catch (IOException ex) {
            /* Favourites are optional data. */
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), StandardCharsets.UTF_8))) {
            for (String pair : pairs) {
                writer.write(pair);
                writer.newLine();
            }
        } catch (IOException ex) {
            /* Ignore write failures. */
        }
    }
}

package currencyconverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads, stores and trims the conversion history kept in {@code history.txt}.
 * Only the most recent {@value #MAX_ENTRIES} conversions are retained.
 */
public final class HistoryManager {

    public static final int MAX_ENTRIES = 20;
    private static final File FILE = new File("history.txt");

    private final List<ConversionRecord> records = new ArrayList<>();

    public HistoryManager() {
        load();
    }

    /** Newest first. */
    public List<ConversionRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public boolean isEmpty() { return records.isEmpty(); }

    public void add(ConversionRecord record, boolean persist) {
        records.add(0, record);
        while (records.size() > MAX_ENTRIES) {
            records.remove(records.size() - 1);
        }
        if (persist) save();
    }

    public void clear() {
        records.clear();
        save();
    }

    /** Counts how often each currency code was used, most used first. */
    public Map<String, Integer> usageByCurrency() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ConversionRecord record : records) {
            counts.merge(record.getFrom(), 1, Integer::sum);
            counts.merge(record.getTo(), 1, Integer::sum);
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());
        Map<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

    private void load() {
        if (!FILE.exists()) return;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ConversionRecord record = ConversionRecord.fromLine(line);
                if (record != null && records.size() < MAX_ENTRIES) {
                    records.add(record);
                }
            }
        } catch (IOException ex) {
            /* A missing or damaged history simply starts empty. */
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), StandardCharsets.UTF_8))) {
            for (ConversionRecord record : records) {
                writer.write(record.toLine());
                writer.newLine();
            }
        } catch (IOException ex) {
            /* Never surface a stack trace to the user. */
        }
    }
}

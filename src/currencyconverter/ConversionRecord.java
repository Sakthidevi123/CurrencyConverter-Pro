package currencyconverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One completed conversion, as stored in the history file. */
public final class ConversionRecord {

    private static final DateTimeFormatter STORAGE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_OUT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_OUT = DateTimeFormatter.ofPattern("hh:mm a");

    private final LocalDateTime timestamp;
    private final double amount;
    private final String from;
    private final String to;
    private final double rate;
    private final double result;

    public ConversionRecord(LocalDateTime timestamp, double amount, String from, String to,
                            double rate, double result) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.rate = rate;
        this.result = result;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public double getAmount() { return amount; }
    public String getFrom()   { return from; }
    public String getTo()     { return to; }
    public double getRate()   { return rate; }
    public double getResult() { return result; }

    public String getFormattedDate() { return timestamp.format(DATE_OUT); }
    public String getFormattedTime() { return timestamp.format(TIME_OUT); }

    /** Serialises the record to a single pipe separated line. */
    public String toLine() {
        return String.join("|",
                timestamp.format(STORAGE),
                String.valueOf(amount),
                from,
                to,
                String.valueOf(rate),
                String.valueOf(result));
    }

    /** Parses a stored line, returning {@code null} when it is malformed. */
    public static ConversionRecord fromLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|");
        if (parts.length < 6) return null;
        try {
            return new ConversionRecord(
                    LocalDateTime.parse(parts[0], STORAGE),
                    Double.parseDouble(parts[1]),
                    parts[2],
                    parts[3],
                    Double.parseDouble(parts[4]),
                    Double.parseDouble(parts[5]));
        } catch (RuntimeException ex) {
            return null;
        }
    }
}

package currencyconverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Writes the conversion history to a CSV file. */
public final class CSVExporter {

    private static final String HEADER = "Date,Time,Amount,From,To,Rate,Result";

    private CSVExporter() { }

    /**
     * Exports the supplied records.
     *
     * @return the file that was written
     * @throws IOException when the file cannot be created or written
     */
    public static File export(List<ConversionRecord> records, File target) throws IOException {
        File file = target != null ? target : new File("history.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();
            for (ConversionRecord record : records) {
                writer.write(String.format("%s,%s,%.2f,%s,%s,%.6f,%.2f",
                        record.getFormattedDate(),
                        record.getFormattedTime(),
                        record.getAmount(),
                        record.getFrom(),
                        record.getTo(),
                        record.getRate(),
                        record.getResult()));
                writer.newLine();
            }
        }
        return file;
    }
}

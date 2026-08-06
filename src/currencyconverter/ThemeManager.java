package currencyconverter;

import java.awt.Color;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Owns the colour palettes and persists the active theme to
 * {@code theme.properties}.
 */
public final class ThemeManager {

    /** A complete colour palette for one theme. */
    public static final class Palette {
        public final String name;
        public final Color primary, accent, background, card, text, subText, border,
                headerStart, headerEnd, success, warning, error, scrollThumb;
        public final boolean dark;

        public Palette(String name, boolean dark, int primary, int accent, int background, int card,
                       int text, int subText, int border, int headerStart, int headerEnd, int scrollThumb) {
            this.name = name;
            this.dark = dark;
            this.primary = new Color(primary);
            this.accent = new Color(accent);
            this.background = new Color(background);
            this.card = new Color(card);
            this.text = new Color(text);
            this.subText = new Color(subText);
            this.border = new Color(border);
            this.headerStart = new Color(headerStart);
            this.headerEnd = new Color(headerEnd);
            this.scrollThumb = new Color(scrollThumb);
            this.success = new Color(0x22C55E);
            this.warning = new Color(0xF59E0B);
            this.error = new Color(0xEF4444);
        }
    }

    private static final File FILE = new File("theme.properties");
    private static final Map<String, Palette> THEMES = new LinkedHashMap<>();

    static {
        THEMES.put("Light",   new Palette("Light",   false, 0x2563EB, 0x3B82F6, 0xF4F7FC, 0xFFFFFF,
                0x1E293B, 0x64748B, 0xE2E8F0, 0x2563EB, 0x3B82F6, 0xCBD5E1));
        THEMES.put("Dark",    new Palette("Dark",    true,  0x3B82F6, 0x60A5FA, 0x1E293B, 0x334155,
                0xF1F5F9, 0x94A3B8, 0x475569, 0x1E3A8A, 0x2563EB, 0x64748B));
        THEMES.put("Blue",    new Palette("Blue",    false, 0x1D4ED8, 0x3B82F6, 0xEFF6FF, 0xFFFFFF,
                0x172554, 0x475569, 0xDBEAFE, 0x1E40AF, 0x3B82F6, 0xBFDBFE));
        THEMES.put("Emerald", new Palette("Emerald", false, 0x059669, 0x10B981, 0xF0FDF4, 0xFFFFFF,
                0x064E3B, 0x4B5563, 0xD1FAE5, 0x047857, 0x10B981, 0xA7F3D0));
        THEMES.put("Purple",  new Palette("Purple",  true,  0x7C3AED, 0xA855F7, 0x1B1533, 0x2A2246,
                0xF3F0FF, 0xA79BC9, 0x3F3663, 0x5B21B6, 0x7C3AED, 0x5B4B8A));
    }

    private Palette current = THEMES.get("Light");

    public ThemeManager() {
        load();
    }

    public static String[] themeNames() {
        return THEMES.keySet().toArray(new String[0]);
    }

    public Palette getPalette() { return current; }

    public String getThemeName() { return current.name; }

    public boolean isDark() { return current.dark; }

    /** Applies a theme by name and stores it on disk. */
    public void applyTheme(String name) {
        Palette palette = THEMES.get(name);
        if (palette == null) return;
        current = palette;
        save();
    }

    /** Toggles between the Light and Dark palettes. */
    public void toggleDarkMode() {
        applyTheme(current.dark ? "Light" : "Dark");
    }

    private void load() {
        if (!FILE.exists()) {
            save();
            return;
        }
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(FILE)) {
            properties.load(in);
            Palette palette = THEMES.get(properties.getProperty("theme", "Light"));
            if (palette != null) current = palette;
        } catch (IOException ex) {
            /* Keep the default theme when the file cannot be read. */
        }
    }

    private void save() {
        Properties properties = new Properties();
        properties.setProperty("theme", current.name);
        try (OutputStream out = new FileOutputStream(FILE)) {
            properties.store(out, "Currency Converter Pro - active theme");
        } catch (IOException ex) {
            /* Ignore: theme persistence is best effort. */
        }
    }
}

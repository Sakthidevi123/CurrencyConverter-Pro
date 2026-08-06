package currencyconverter;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/** Thin wrapper around the system clipboard. */
public final class ClipboardManager {

    private ClipboardManager() { }

    /** Copies the text; returns false when the clipboard is unavailable. */
    public static boolean copy(String text) {
        if (text == null || text.isBlank()) return false;
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(text), null);
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }
}

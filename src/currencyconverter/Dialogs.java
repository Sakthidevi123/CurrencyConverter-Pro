package currencyconverter;

import currencyconverter.ui.RoundedButton;
import currencyconverter.ui.RoundedDialog;
import currencyconverter.ui.RoundedPanel;
import java.awt.*;
import javax.swing.*;

/**
 * Factory for the professional message dialogs used across the application.
 * Every dialog shares one layout so the look stays consistent.
 */
public final class Dialogs {

    public enum Kind { SUCCESS, INFO, WARNING, ERROR }

    private Dialogs() { }

    public static void message(JFrame owner, ThemeManager.Palette palette,
                               Kind kind, String title, String body) {
        RoundedDialog dialog = new RoundedDialog(owner, title);
        dialog.setSurface(palette.card);

        RoundedPanel content = dialog.getRoundedContent();
        content.setBorder(BorderFactory.createEmptyBorder(26, 30, 22, 30));

        Color accent = switch (kind) {
            case SUCCESS -> palette.success;
            case WARNING -> palette.warning;
            case ERROR   -> palette.error;
            default      -> palette.primary;
        };
        String glyph = switch (kind) {
            case SUCCESS -> "\u2713";
            case WARNING -> "!";
            case ERROR   -> "\u2715";
            default      -> "i";
        };

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        top.setOpaque(false);

        JLabel badge = new JLabel(glyph, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 38));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setPreferredSize(new Dimension(42, 42));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        badge.setForeground(accent);
        top.add(badge);

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 17));
        heading.setForeground(palette.text);
        top.add(heading);

        JLabel text = new JLabel("<html><body style='width:300px'>" + escape(body) + "</body></html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        text.setForeground(palette.subText);
        text.setBorder(BorderFactory.createEmptyBorder(16, 4, 20, 4));

        RoundedButton ok = new RoundedButton("OK", RoundedButton.Variant.PRIMARY);
        ok.applyPalette(palette.primary, palette.accent, palette.card, palette.text, palette.border);
        ok.addActionListener(e -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(ok);

        content.add(top, BorderLayout.NORTH);
        content.add(text, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);

        dialog.showCentered();
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

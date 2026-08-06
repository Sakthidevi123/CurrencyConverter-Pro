package currencyconverter.ui;

import java.awt.*;
import javax.swing.*;

/**
 * Lightweight, non blocking toast notification rendered inside the frame's
 * layered pane. Fades in, waits, then fades out.
 */
public final class Toast {

    public enum Type { SUCCESS, INFO, WARNING, ERROR }

    private Toast() { }

    public static void show(JFrame frame, String message, Type type) {
        if (frame == null || !frame.isShowing()) return;

        JLayeredPane layers = frame.getLayeredPane();
        final ToastPanel panel = new ToastPanel(message, type);
        Dimension size = panel.getPreferredSize();
        int x = (layers.getWidth() - size.width) / 2;
        int y = layers.getHeight() - size.height - 54;
        panel.setBounds(x, y, size.width, size.height);
        layers.add(panel, JLayeredPane.POPUP_LAYER);
        layers.repaint();

        Timer fadeIn = new Timer(14, null);
        fadeIn.addActionListener(e -> {
            panel.alpha = Math.min(1f, panel.alpha + 0.1f);
            panel.repaint();
            if (panel.alpha >= 1f) fadeIn.stop();
        });
        fadeIn.start();

        Timer hold = new Timer(2200, e -> {
            Timer fadeOut = new Timer(14, null);
            fadeOut.addActionListener(ev -> {
                panel.alpha = Math.max(0f, panel.alpha - 0.08f);
                panel.repaint();
                if (panel.alpha <= 0f) {
                    fadeOut.stop();
                    layers.remove(panel);
                    layers.repaint();
                }
            });
            fadeOut.start();
        });
        hold.setRepeats(false);
        hold.start();
    }

    /** Painted body of the toast. */
    private static class ToastPanel extends JComponent {
        private final String message;
        private final Color accent;
        private final String glyph;
        private float alpha = 0f;

        ToastPanel(String message, Type type) {
            this.message = message;
            switch (type) {
                case SUCCESS -> { accent = new Color(0x22C55E); glyph = "\u2713"; }
                case WARNING -> { accent = new Color(0xF59E0B); glyph = "!"; }
                case ERROR   -> { accent = new Color(0xEF4444); glyph = "\u2715"; }
                default      -> { accent = new Color(0x3B82F6); glyph = "i"; }
            }
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(Math.min(520, fm.stringWidth(message) + 82), 50);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            g2.setColor(new Color(15, 23, 42, 240));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(accent);
            g2.fillRoundRect(0, 0, 5, getHeight(), 8, 8);
            g2.fillOval(18, getHeight() / 2 - 10, 20, 20);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics gm = g2.getFontMetrics();
            g2.drawString(glyph, 28 - gm.stringWidth(glyph) / 2, getHeight() / 2 + 4);

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(message, 50, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}

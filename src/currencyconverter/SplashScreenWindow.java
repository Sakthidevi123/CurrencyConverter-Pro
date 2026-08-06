package currencyconverter;

import java.awt.*;
import javax.swing.*;

/** Branded, undecorated splash shown while the application boots. */
public final class SplashScreenWindow extends JWindow {

    private int progress;
    private final Timer timer;

    public SplashScreenWindow() {
        setSize(460, 280);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
        setContentPane(new SplashPanel());
        timer = new Timer(22, e -> {
            progress = Math.min(100, progress + 2);
            repaint();
        });
    }

    /** Shows the splash, runs the progress animation, then disposes. */
    public void showFor(int millis, Runnable onFinished) {
        setVisible(true);
        timer.start();
        Timer close = new Timer(millis, e -> {
            timer.stop();
            dispose();
            onFinished.run();
        });
        close.setRepeats(false);
        close.start();
    }

    /** Painted splash body. */
    private class SplashPanel extends JPanel {
        SplashPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, new Color(0x1D4ED8), w, h, new Color(0x3B82F6)));
            g2.fillRoundRect(0, 0, w, h, 26, 26);

            g2.setColor(new Color(255, 255, 255, 26));
            g2.fillOval(w - 120, -60, 220, 220);
            g2.fillOval(-70, h - 90, 180, 180);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
            g2.drawString("Currency Converter", 46, 116);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
            g2.drawString("Pro", 46, 152);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString("Real time exchange rates \u00B7 Version " + CurrencyConvert.VERSION, 46, 182);

            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillRoundRect(46, 214, w - 92, 6, 6, 6);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(46, 214, (int) ((w - 92) * (progress / 100f)), 6, 6, 6);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(255, 255, 255, 190));
            g2.drawString("Loading exchange rate engine\u2026", 46, 244);
            g2.dispose();
        }
    }
}

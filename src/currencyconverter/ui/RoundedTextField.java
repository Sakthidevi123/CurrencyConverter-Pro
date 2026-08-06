package currencyconverter.ui;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

/** Text field with rounded background, placeholder text and a focus ring. */
public class RoundedTextField extends JTextField {

    private int arc = 14;
    private String placeholder = "";
    private Color surface = Color.WHITE;
    private Color borderColor = new Color(0xE2E8F0);
    private Color focusColor = new Color(0x2563EB);
    private Color placeholderColor = new Color(0x94A3B8);
    private boolean focused;

    public RoundedTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
        setForeground(new Color(0x1E293B));
        setCaretColor(new Color(0x2563EB));
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
            @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
        });
    }

    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; repaint(); }

    public void applyPalette(Color surface, Color border, Color focus, Color text, Color hint) {
        this.surface = surface;
        this.borderColor = border;
        this.focusColor = focus;
        this.placeholderColor = hint;
        setForeground(text);
        setCaretColor(focus);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(surface);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.setStroke(new BasicStroke(focused ? 2f : 1f));
        g2.setColor(focused ? focusColor : borderColor);
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
        g2.dispose();

        super.paintComponent(g);

        if (getText().isEmpty() && !placeholder.isEmpty()) {
            Graphics2D gp = (Graphics2D) g.create();
            gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gp.setColor(placeholderColor);
            gp.setFont(getFont());
            FontMetrics fm = gp.getFontMetrics();
            Insets in = getInsets();
            gp.drawString(placeholder, in.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            gp.dispose();
        }
    }
}

package currencyconverter.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * Rounded, flat combo box with a custom arrow and a themed popup list.
 *
 * @param <E> element type
 */
public class RoundedComboBox<E> extends JComboBox<E> {

    private int arc = 14;
    private Color surface = Color.WHITE;
    private Color borderColor = new Color(0xE2E8F0);
    private Color textColor = new Color(0x1E293B);
    private Color accent = new Color(0x2563EB);

    public RoundedComboBox(E[] items) {
        super(items);
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));
        setMaximumRowCount(10);
        setUI(new FlatComboUI());
        setRenderer(new FlatRenderer());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void applyPalette(Color surface, Color border, Color text, Color accent) {
        this.surface = surface;
        this.borderColor = border;
        this.textColor = text;
        this.accent = accent;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(surface);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        /* selected value */
        Object value = getSelectedItem();
        if (value != null) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();
            String text = value.toString();
            int maxWidth = getWidth() - 46;
            while (fm.stringWidth(text) > maxWidth && text.length() > 4) {
                text = text.substring(0, text.length() - 2);
            }
            g2.drawString(text, 14, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        }

        /* chevron */
        int cx = getWidth() - 22;
        int cy = getHeight() / 2 - 1;
        g2.setColor(accent);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
        g2.drawLine(cx, cy + 3, cx + 5, cy - 2);
        g2.dispose();
    }

    /** Strips the default border/arrow button from the combo box. */
    private class FlatComboUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton();
            button.setVisible(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            return button;
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            popup.setBorder(BorderFactory.createLineBorder(borderColor));
            return popup;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            /* handled in paintComponent */
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            /* handled in paintComponent */
        }
    }

    /** List renderer that matches the active theme. */
    private class FlatRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(accent);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(surface);
                label.setForeground(textColor);
            }
            return label;
        }
    }
}

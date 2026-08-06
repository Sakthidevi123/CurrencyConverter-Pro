package currencyconverter.ui;

import java.awt.*;
import javax.swing.*;

/**
 * A panel painted with rounded corners and an optional 1px border.
 * Used as the base container for every card surface in the application.
 */
public class RoundedPanel extends JPanel {

    private int arc = 22;
    private Color fill = Color.WHITE;
    private Color borderColor = null;

    public RoundedPanel() {
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout, int arc) {
        this(layout);
        this.arc = arc;
    }

    public void setArc(int arc) { this.arc = arc; repaint(); }
    public int getArc() { return arc; }

    public void setFill(Color fill) { this.fill = fill; repaint(); }
    public Color getFill() { return fill; }

    public void setBorderColor(Color c) { this.borderColor = c; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}

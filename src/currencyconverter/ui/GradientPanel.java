package currencyconverter.ui;

import java.awt.*;
import javax.swing.*;

public class GradientPanel extends JPanel {

    private Color start = new Color(0x2563EB);
    private Color end = new Color(0x3B82F6);
    private boolean horizontal = true;

    public GradientPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public void setColors(Color start, Color end) {
        this.start = start;
        this.end = end;
        repaint();
    }

    public void setHorizontal(boolean horizontal) { this.horizontal = horizontal; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint paint = horizontal
                ? new GradientPaint(0, 0, start, getWidth(), 0, end)
                : new GradientPaint(0, 0, start, 0, getHeight(), end);
        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}

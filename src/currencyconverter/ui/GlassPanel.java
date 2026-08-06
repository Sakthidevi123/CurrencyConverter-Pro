package currencyconverter.ui;

import java.awt.*;
import javax.swing.*;

public class GlassPanel extends JPanel {

    private int arc = 20;
    private float opacity = 0.18f;
    private Color tint = Color.WHITE;

    public GlassPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public void setTint(Color tint) { this.tint = tint; repaint(); }
    public void setOpacity(float opacity) { this.opacity = opacity; repaint(); }
    public void setArc(int arc) { this.arc = arc; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2.setColor(tint);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, opacity + 0.15f)));
        g2.setColor(new Color(255, 255, 255, 90));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}

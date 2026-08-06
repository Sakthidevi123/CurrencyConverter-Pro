package currencyconverter.ui;

import java.awt.*;
import javax.swing.*;

/**
 * A container that paints a soft drop shadow behind a rounded surface.
 * The shadow is drawn as a set of progressively lighter rounded rectangles,
 * which keeps rendering cheap while still looking soft.
 */
public class ShadowPanel extends JPanel {

    private int arc = 24;
    private int shadowSize = 10;
    private float shadowOpacity = 0.12f;
    private Color fill = Color.WHITE;

    public ShadowPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize + 2, shadowSize));
    }

    public void setArc(int arc) { this.arc = arc; repaint(); }
    public void setFill(Color fill) { this.fill = fill; repaint(); }
    public Color getFill() { return fill; }

    public void setShadowOpacity(float opacity) { this.shadowOpacity = opacity; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        for (int i = shadowSize; i > 0; i--) {
            float alpha = shadowOpacity / shadowSize;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.BLACK);
            g2.fillRoundRect(shadowSize - i, shadowSize - i + 2,
                    w - 2 * (shadowSize - i), h - 2 * (shadowSize - i) - 2,
                    arc + i, arc + i);
        }

        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(fill);
        g2.fillRoundRect(shadowSize, shadowSize, w - 2 * shadowSize, h - 2 * shadowSize - 2, arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}

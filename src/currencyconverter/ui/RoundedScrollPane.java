package currencyconverter.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** Scroll pane with a slim, modern, themable scrollbar and no outer border. */
public class RoundedScrollPane extends JScrollPane {

    private Color thumbColor = new Color(0xCBD5E1);
    private Color trackColor = new Color(0, 0, 0, 0);

    public RoundedScrollPane(Component view) {
        super(view);
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);
        getViewport().setOpaque(false);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        getVerticalScrollBar().setUnitIncrement(16);
        getVerticalScrollBar().setPreferredSize(new Dimension(9, 0));
        getVerticalScrollBar().setUI(new SlimScrollBarUI());
        getVerticalScrollBar().setOpaque(false);
    }

    public void applyPalette(Color thumb) {
        this.thumbColor = thumb;
        repaint();
    }

    private class SlimScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() { }

        @Override protected JButton createDecreaseButton(int orientation) { return zeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return zeroButton(); }

        private JButton zeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 2, r.y, r.width - 4, r.height, 8, 8);
            g2.dispose();
        }
    }
}

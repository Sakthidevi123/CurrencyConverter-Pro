package currencyconverter.ui;

import java.awt.*;
import java.awt.geom.Arc2D;
import javax.swing.*;

public class LoadingSpinner extends JComponent {

    private int angle;
    private final Timer timer;
    private Color color = new Color(0x2563EB);

    public LoadingSpinner() {
        setPreferredSize(new Dimension(22, 22));
        setVisible(false);
        timer = new Timer(28, e -> {
            angle = (angle + 12) % 360;
            repaint();
        });
    }

    public void setColor(Color color) { this.color = color; repaint(); }

    public void start() {
        setVisible(true);
        if (!timer.isRunning()) timer.start();
    }

    public void stop() {
        timer.stop();
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = Math.min(getWidth(), getHeight()) - 4;
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 55));
        g2.draw(new Arc2D.Double(2, 2, size, size, 0, 360, Arc2D.OPEN));
        g2.setColor(color);
        g2.draw(new Arc2D.Double(2, 2, size, size, angle, 100, Arc2D.OPEN));
        g2.dispose();
    }
}

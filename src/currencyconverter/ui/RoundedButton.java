package currencyconverter.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class RoundedButton extends JButton {

    public enum Variant { PRIMARY, SECONDARY, GHOST, DANGER }

    private int arc = 16;
    private Variant variant = Variant.PRIMARY;

    private Color base = new Color(0x2563EB);
    private Color hover = new Color(0x3B82F6);
    private Color pressed = new Color(0x1D4ED8);
    private Color textColor = Color.WHITE;
    private Color borderColor = null;

    private boolean hovered;
    private boolean armedDown;

    /* ripple state */
    private boolean rippleEnabled = true;
    private float rippleRadius;
    private float rippleAlpha;
    private Point rippleOrigin;
    private Timer rippleTimer;

    /* hover fade state */
    private float hoverProgress;
    private Timer hoverTimer;
    private boolean animationsEnabled = true;

    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        installListeners();
    }

    public RoundedButton(String text, Variant variant) {
        this(text);
        setVariant(variant);
    }

    private void installListeners() {
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; animateHover(true); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; armedDown = false; animateHover(false); }
            @Override public void mousePressed(MouseEvent e) { armedDown = true; startRipple(e.getPoint()); repaint(); }
            @Override public void mouseReleased(MouseEvent e){ armedDown = false; repaint(); }
        });
    }

    public void setAnimationsEnabled(boolean enabled) {
        this.animationsEnabled = enabled;
        this.rippleEnabled = enabled;
    }

    public void setArc(int arc) { this.arc = arc; repaint(); }

    public final void setVariant(Variant variant) {
        this.variant = variant;
        applyVariantColors();
        repaint();
    }

    public Variant getVariant() { return variant; }

    /** Re-applies palette colours; called by the theme manager on theme change. */
    public void applyPalette(Color primary, Color accent, Color surface, Color text, Color border) {
        switch (variant) {
            case PRIMARY -> {
                base = primary; hover = accent; pressed = primary.darker();
                textColor = Color.WHITE; borderColor = null;
            }
            case SECONDARY -> {
                base = surface; hover = blend(surface, primary, 0.10f); pressed = blend(surface, primary, 0.20f);
                textColor = text; borderColor = border;
            }
            case GHOST -> {
                base = new Color(0, 0, 0, 0); hover = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 28);
                pressed = new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 52);
                textColor = text; borderColor = null;
            }
            case DANGER -> {
                base = new Color(0xEF4444); hover = new Color(0xF87171); pressed = new Color(0xDC2626);
                textColor = Color.WHITE; borderColor = null;
            }
        }
        repaint();
    }

    private void applyVariantColors() {
        applyPalette(new Color(0x2563EB), new Color(0x3B82F6), Color.WHITE,
                new Color(0x1E293B), new Color(0xE2E8F0));
    }

    private static Color blend(Color a, Color b, float ratio) {
        return new Color(
                Math.round(a.getRed() * (1 - ratio) + b.getRed() * ratio),
                Math.round(a.getGreen() * (1 - ratio) + b.getGreen() * ratio),
                Math.round(a.getBlue() * (1 - ratio) + b.getBlue() * ratio));
    }

    private void animateHover(boolean in) {
        if (!animationsEnabled) {
            hoverProgress = in ? 1f : 0f;
            repaint();
            return;
        }
        if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
        hoverTimer = new Timer(12, null);
        hoverTimer.addActionListener(e -> {
            hoverProgress += in ? 0.14f : -0.14f;
            if (hoverProgress >= 1f) { hoverProgress = 1f; hoverTimer.stop(); }
            if (hoverProgress <= 0f) { hoverProgress = 0f; hoverTimer.stop(); }
            repaint();
        });
        hoverTimer.start();
    }

    private void startRipple(Point origin) {
        if (!rippleEnabled) return;
        rippleOrigin = origin;
        rippleRadius = 0f;
        rippleAlpha = 0.35f;
        if (rippleTimer != null && rippleTimer.isRunning()) rippleTimer.stop();
        rippleTimer = new Timer(12, null);
        rippleTimer.addActionListener(e -> {
            rippleRadius += Math.max(getWidth(), getHeight()) / 14f;
            rippleAlpha -= 0.022f;
            if (rippleAlpha <= 0f) {
                rippleAlpha = 0f;
                rippleTimer.stop();
            }
            repaint();
        });
        rippleTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color background;
        if (!isEnabled()) {
            background = blend(base.getAlpha() == 0 ? Color.LIGHT_GRAY : base, Color.GRAY, 0.55f);
        } else if (armedDown) {
            background = pressed;
        } else {
            background = blend(base, hover, hoverProgress);
        }

        if (background.getAlpha() > 0) {
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        }

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        }

        if (rippleAlpha > 0 && rippleOrigin != null) {
            Shape clip = new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setClip(clip);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, rippleAlpha));
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Float(rippleOrigin.x - rippleRadius, rippleOrigin.y - rippleRadius,
                    rippleRadius * 2, rippleRadius * 2));
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setClip(null);
        }

        FontMetrics fm = g2.getFontMetrics(getFont());
        g2.setFont(getFont());
        g2.setColor(isEnabled() ? textColor : new Color(0xF1F5F9));
        System.out.println("Button text = [" + getText() + "]");
        String label = getText();
        int tx = (getWidth() - fm.stringWidth(label)) / 2;
        int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(label, tx, ty);
        g2.dispose();
    }

    public boolean isHovered() { return hovered; }
}

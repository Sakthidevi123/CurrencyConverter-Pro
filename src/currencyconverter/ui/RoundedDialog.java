package currencyconverter.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Undecorated, rounded, draggable modal dialog used for every message,
 * confirmation and secondary window in the application.
 */
public class RoundedDialog extends JDialog {

    private final RoundedPanel content;
    private Point dragOffset;

    public RoundedDialog(Window owner, String title) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        content = new RoundedPanel(new BorderLayout(), 20);
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setContentPane(content);

        MouseAdapter dragger = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragOffset = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                if (dragOffset == null) return;
                Point p = e.getLocationOnScreen();
                setLocation(p.x - dragOffset.x, p.y - dragOffset.y);
            }
        };
        content.addMouseListener(dragger);
        content.addMouseMotionListener(dragger);

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public RoundedPanel getRoundedContent() { return content; }

    public void setSurface(Color color) { content.setFill(color); }

    /** Centres the dialog over its owner (or the screen when there is none). */
    public void showCentered() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}

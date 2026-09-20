package opampapp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A rounded, gradient-filled button styled after the reference
 * application (light blue card with a soft border and bold label).
 * Supports a "selected" state so the active menu item can stay
 * highlighted.
 */
public class StyledButton extends JButton {

    private boolean selected = false;

    public StyledButton(String text) {
        super(text);
        setFont(UITheme.FONT_BUTTON);
        setForeground(UITheme.BTN_TEXT);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.CENTER);
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void setSelectedState(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 14;

        Color top, bottom;
        if (selected) {
            top = UITheme.BTN_TOP_ACTIVE;
            bottom = UITheme.BTN_BOTTOM_ACTIVE;
        } else if (getModel().isRollover()) {
            top = UITheme.BTN_TOP_HOVER;
            bottom = UITheme.BTN_BOTTOM_HOVER;
        } else {
            top = UITheme.BTN_TOP;
            bottom = UITheme.BTN_BOTTOM;
        }

        GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bottom);
        g2.setPaint(gp);
        RoundRectangle2D shape = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, arc, arc);
        g2.fill(shape);

        g2.setStroke(new BasicStroke(selected ? 2.2f : 1.4f));
        g2.setColor(UITheme.BTN_BORDER);
        g2.draw(shape);

        g2.dispose();
        super.paintComponent(g);
    }
}

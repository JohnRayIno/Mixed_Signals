package opampapp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Draws a simplified schematic diagram for the selected op-amp
 * application. Every element (op-amp triangle, resistor, capacitor,
 * ground symbol, wire) is rendered with Graphics2D so no external
 * image assets are required.
 */
public class CircuitDiagramPanel extends JPanel {

    private final CircuitType type;

    public CircuitDiagramPanel(CircuitType type) {
        this.type = type;
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(0xC7D3DE), 1, true));
        setPreferredSize(new Dimension(480, 260));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UITheme.WIRE);
        g2.setStroke(new BasicStroke(1.8f));
        g2.setFont(UITheme.FONT_DIAGRAM);

        switch (type) {
            case INVERTING:      drawInverting(g2); break;
            case NON_INVERTING:  drawNonInverting(g2); break;
            case DIFFERENTIAL:   drawDifferential(g2); break;
            case INTEGRATOR:     drawIntegrator(g2); break;
            case I_TO_V:         drawItoV(g2); break;
            case V_TO_I:         drawVtoI(g2); break;
        }
        g2.dispose();
    }

    // ---------------------------------------------------------------
    // Individual schematics
    // ---------------------------------------------------------------

    private void drawInverting(Graphics2D g2) {
        int opX = 230, opY = 90, opW = 90, opH = 80;
        int minusY = opY + 20, plusY = opY + opH - 20;

        label(g2, "Vin", 20, minusY - 8);
        drawWire(g2, 45, minusY, 90, minusY);
        drawResistorH(g2, 90, minusY, 60, "Rin");
        drawWire(g2, 150, minusY, opX, minusY);
        dot(g2, 150, minusY);

        // feedback path
        drawWire(g2, 150, minusY, 150, 40);
        drawWire(g2, 150, 40, opX + opW + 70, 40);
        drawResistorH(g2, opX + opW + 10, 40, 60, "Rf");
        drawWire(g2, opX + opW + 70, 40, opX + opW + 70, opY + opH / 2);
        drawWire(g2, opX + opW + 70, opY + opH / 2, opX + opW, opY + opH / 2);

        // + input to ground
        drawWire(g2, opX - 40, plusY, opX, plusY);
        drawWire(g2, opX - 40, plusY, opX - 40, plusY + 30);
        drawGround(g2, opX - 40, plusY + 30);

        drawOpAmp(g2, opX, opY, opW, opH);

        // output
        drawWire(g2, opX + opW, opY + opH / 2, opX + opW + 110, opY + opH / 2);
        label(g2, "Vout", opX + opW + 115, opY + opH / 2 + 5);
    }

    private void drawNonInverting(Graphics2D g2) {
        int opX = 225, opY = 85, opW = 90, opH = 90;
        int minusY = opY + 20, plusY = opY + opH - 20;

        // Route Vin below the feedback network directly to the + input.
        label(g2, "Vin", 20, plusY + 40);
        drawWire(g2, 52, plusY + 35, opX - 30, plusY + 35);
        drawWire(g2, opX - 30, plusY + 35, opX - 30, plusY);
        drawWire(g2, opX - 30, plusY, opX, plusY);

        // - input node: R1 to ground, Rf to output
        int nodeX = 165;
        drawWire(g2, nodeX, minusY, opX, minusY);
        dot(g2, nodeX, minusY);
        drawResistorV(g2, nodeX, minusY, 45, "R1");
        drawWire(g2, nodeX, minusY + 45, nodeX, minusY + 70);
        drawGround(g2, nodeX, minusY + 70);

        drawWire(g2, nodeX, minusY, nodeX, 40);
        drawWire(g2, nodeX, 40, opX + opW + 70, 40);
        drawResistorH(g2, nodeX + 20, 40, 60, "Rf");
        drawWire(g2, opX + opW + 70, 40, opX + opW + 70, opY + opH / 2);
        drawWire(g2, opX + opW + 70, opY + opH / 2, opX + opW, opY + opH / 2);

        drawOpAmp(g2, opX, opY, opW, opH);

        drawWire(g2, opX + opW, opY + opH / 2, opX + opW + 110, opY + opH / 2);
        label(g2, "Vout", opX + opW + 115, opY + opH / 2 + 5);
    }

    private void drawDifferential(Graphics2D g2) {
        int opX = 230, opY = 80, opW = 90, opH = 90;
        int minusY = opY + 22, plusY = opY + opH - 22;

        // V1 -> R1 -> (-) input, Rf feedback
        label(g2, "V1", 20, minusY - 8);
        drawWire(g2, 40, minusY, 80, minusY);
        drawResistorH(g2, 80, minusY, 60, "R1");
        drawWire(g2, 140, minusY, opX, minusY);
        dot(g2, 140, minusY);
        drawWire(g2, 140, minusY, 140, 35);
        drawWire(g2, 140, 35, opX + opW + 70, 35);
        drawResistorH(g2, 160, 35, 60, "Rf");
        drawWire(g2, opX + opW + 70, 35, opX + opW + 70, opY + opH / 2);
        drawWire(g2, opX + opW + 70, opY + opH / 2, opX + opW, opY + opH / 2);

        // V2 -> R2 -> (+) input -> R3 to ground
        label(g2, "V2", 20, plusY + 4);
        drawWire(g2, 40, plusY, 80, plusY);
        drawResistorH(g2, 80, plusY, 60, "R2");
        drawWire(g2, 140, plusY, opX, plusY);
        dot(g2, 140, plusY);
        drawResistorV(g2, 140, plusY, 45, "R3");
        drawWire(g2, 140, plusY + 45, 140, plusY + 65);
        drawGround(g2, 140, plusY + 65);

        drawOpAmp(g2, opX, opY, opW, opH);

        int outY = opY + opH / 2;
        drawWire(g2, opX + opW, outY, opX + opW + 110, outY);
        label(g2, "Vout", opX + opW + 115, outY + 5);
    }

    private void drawIntegrator(Graphics2D g2) {
        int opX = 230, opY = 90, opW = 90, opH = 80;
        int minusY = opY + 20, plusY = opY + opH - 20;

        label(g2, "Vin", 20, minusY - 8);
        drawWire(g2, 45, minusY, 90, minusY);
        drawResistorH(g2, 90, minusY, 60, "R");
        drawWire(g2, 150, minusY, opX, minusY);
        dot(g2, 150, minusY);

        drawWire(g2, 150, minusY, 150, 40);
        drawWire(g2, 150, 40, opX + opW + 70, 40);
        drawCapacitorH(g2, opX + opW - 20, 40, "C");
        drawWire(g2, opX + opW + 70, 40, opX + opW + 70, opY + opH / 2);
        drawWire(g2, opX + opW + 70, opY + opH / 2, opX + opW, opY + opH / 2);

        drawWire(g2, opX - 40, plusY, opX, plusY);
        drawWire(g2, opX - 40, plusY, opX - 40, plusY + 30);
        drawGround(g2, opX - 40, plusY + 30);

        drawOpAmp(g2, opX, opY, opW, opH);

        drawWire(g2, opX + opW, opY + opH / 2, opX + opW + 110, opY + opH / 2);
        label(g2, "Vout", opX + opW + 115, opY + opH / 2 + 5);
    }

    private void drawItoV(Graphics2D g2) {
        int opX = 230, opY = 90, opW = 90, opH = 80;
        int minusY = opY + 20, plusY = opY + opH - 20;

    
        int nodeX = 150;
        drawWire(g2, nodeX, minusY, opX, minusY);
        dot(g2, nodeX, minusY);

    
        int r = 15;
        int srcTop = minusY + 40;
        drawWire(g2, nodeX, minusY, nodeX, srcTop);
        g2.drawOval(nodeX - r, srcTop, 2 * r, 2 * r);
        drawArrow(g2, nodeX, srcTop + 7, nodeX, srcTop + 2 * r - 7);
        drawWire(g2, nodeX, srcTop + 2 * r, nodeX, srcTop + 2 * r + 20);
        drawGround(g2, nodeX, srcTop + 2 * r + 20);
        label(g2, "Iin", nodeX - 45, srcTop + r + 5);

    
        drawWire(g2, nodeX, minusY, nodeX, 40);
        drawWire(g2, nodeX, 40, opX + opW + 70, 40);
        drawResistorH(g2, opX + opW + 10, 40, 60, "Rf");
        drawWire(g2, opX + opW + 70, 40, opX + opW + 70, opY + opH / 2);
        drawWire(g2, opX + opW + 70, opY + opH / 2, opX + opW, opY + opH / 2);

    
        drawWire(g2, opX - 40, plusY, opX, plusY);
        drawWire(g2, opX - 40, plusY, opX - 40, plusY + 30);
        drawGround(g2, opX - 40, plusY + 30);

        drawOpAmp(g2, opX, opY, opW, opH);

    
        drawWire(g2, opX + opW, opY + opH / 2, opX + opW + 110, opY + opH / 2);
        label(g2, "Vout", opX + opW + 115, opY + opH / 2 + 5);
    }


    private void drawVtoI(Graphics2D g2) {
        int opX = 225, opY = 85, opW = 90, opH = 90;
        int minusY = opY + 20, plusY = opY + opH - 20;

        // Route Vin below the feedback resistor so the two paths do not cross.
        label(g2, "Vin", 20, plusY + 40);
        drawWire(g2, 52, plusY + 35, opX - 30, plusY + 35);
        drawWire(g2, opX - 30, plusY + 35, opX - 30, plusY);
        drawWire(g2, opX - 30, plusY, opX, plusY);

        int nodeX = 165;
        drawWire(g2, nodeX, minusY, opX, minusY);
        dot(g2, nodeX, minusY);
        drawResistorV(g2, nodeX, minusY, 45, "R1");
        drawWire(g2, nodeX, minusY + 45, nodeX, minusY + 70);
        drawGround(g2, nodeX, minusY + 70);

        drawWire(g2, nodeX, minusY, nodeX, 35);
        drawWire(g2, nodeX, 35, opX + opW + 90, 35);
        drawWire(g2, opX + opW + 90, 35, opX + opW + 90, opY + opH / 2);

        drawOpAmp(g2, opX, opY, opW, opH);

        int outY = opY + opH / 2;
        drawWire(g2, opX + opW, outY, opX + opW + 90, outY);
        dot(g2, opX + opW + 90, outY);
        drawResistorV(g2, opX + opW + 90, outY, 55, "RL");
        drawWire(g2, opX + opW + 90, outY + 55, opX + opW + 90, outY + 75);
        drawGround(g2, opX + opW + 90, outY + 75);
        drawArrow(g2, opX + opW + 130, outY + 18, opX + opW + 130, outY + 45);
        label(g2, "Iout", opX + opW + 140, outY + 35);
    }

    // ---------------------------------------------------------------
    // Reusable primitives
    // ---------------------------------------------------------------

    /** Draws the op-amp triangle and returns nothing; +/- labeled inside. */
    private void drawOpAmp(Graphics2D g2, int x, int y, int w, int h) {
        Polygon tri = new Polygon();
        tri.addPoint(x, y);
        tri.addPoint(x, y + h);
        tri.addPoint(x + w, y + h / 2);
        g2.setColor(UITheme.OPAMP_FILL);
        g2.fillPolygon(tri);
        g2.setColor(UITheme.WIRE);
        g2.drawPolygon(tri);

        g2.setFont(UITheme.FONT_LABEL_B);
        g2.drawString("-", x + 10, y + 26);
        g2.drawString("+", x + 10, y + h - 14);
    }

    private void drawResistorH(Graphics2D g2, int x, int y, int w, String label) {
        int zig = 6;
        int segs = 6;
        int segW = (w - 20) / segs;
        Path2D p = new Path2D.Double();
        p.moveTo(x, y);
        p.lineTo(x + 10, y);
        int cx = x + 10;
        boolean up = true;
        for (int i = 0; i < segs; i++) {
            int ny = up ? y - zig : y + zig;
            p.lineTo(cx + segW, ny);
            cx += segW;
            up = !up;
        }
        p.lineTo(x + w, y);
        g2.draw(p);
        g2.setFont(UITheme.FONT_DIAGRAM);
        g2.drawString(label, x + w / 2 - 10, y - 12);
    }

    private void drawResistorV(Graphics2D g2, int x, int y, int h, String label) {
        int zig = 6;
        int segs = 6;
        int segH = (h - 20) / segs;
        Path2D p = new Path2D.Double();
        p.moveTo(x, y);
        p.lineTo(x, y + 10);
        int cy = y + 10;
        boolean right = true;
        for (int i = 0; i < segs; i++) {
            int nx = right ? x + zig : x - zig;
            p.lineTo(nx, cy + segH);
            cy += segH;
            right = !right;
        }
        p.lineTo(x, y + h);
        g2.draw(p);
        g2.setFont(UITheme.FONT_DIAGRAM);
        g2.drawString(label, x + 10, y + h / 2 + 4);
    }

    private void drawCapacitorH(Graphics2D g2, int x, int y, String label) {
        drawWire(g2, x, y, x + 16, y);
        g2.drawLine(x + 16, y - 10, x + 16, y + 10);
        g2.drawLine(x + 22, y - 10, x + 22, y + 10);
        drawWire(g2, x + 22, y, x + 38, y);
        g2.setFont(UITheme.FONT_DIAGRAM);
        g2.drawString(label, x + 10, y - 14);
    }

    private void drawGround(Graphics2D g2, int x, int y) {
        g2.drawLine(x - 10, y, x + 10, y);
        g2.drawLine(x - 6, y + 5, x + 6, y + 5);
        g2.drawLine(x - 2, y + 10, x + 2, y + 10);
    }

    private void drawWire(Graphics2D g2, double x1, double y1, double x2, double y2) {
        g2.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int len = 8;
        int ax = (int) (x2 - len * Math.cos(angle - Math.PI / 6));
        int ay = (int) (y2 - len * Math.sin(angle - Math.PI / 6));
        int bx = (int) (x2 - len * Math.cos(angle + Math.PI / 6));
        int by = (int) (y2 - len * Math.sin(angle + Math.PI / 6));
        g2.drawLine(x2, y2, ax, ay);
        g2.drawLine(x2, y2, bx, by);
    }

    private void dot(Graphics2D g2, int x, int y) {
        g2.fillOval(x - 3, y - 3, 6, 6);
    }

    private void label(Graphics2D g2, String text, int x, int y) {
        g2.setFont(UITheme.FONT_LABEL_B);
        g2.drawString(text, x, y);
    }
}

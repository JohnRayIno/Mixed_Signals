package opampapp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;

/**
 * Two-graph waveform display for the integrator page, styled after the
 * reference figure: the input plot (Vin, triangle wave) on the left, an
 * integral arrow in the middle, and the output plot (Vo, square wave)
 * on the right.
 *
 * The display is driven by the calculation:
 *  - Before "Calculate" both traces are flat lines on the zero axis.
 *  - After "Calculate" the triangle's height follows Vin, the square's
 *    height follows the solved Vout (both on one shared voltage scale,
 *    so their relative size is visible), a negative value flips its
 *    trace, and the time ticks are multiples of the entered t (ms), one
 *    half-cycle per tick.
 */
public class IntegratorWaveformPanel extends JPanel {

    private static final DecimalFormat VOLT_FMT = new DecimalFormat("0.###");
    private static final DecimalFormat TIME_FMT = new DecimalFormat("0.##");
    private static final Color GRID = new Color(0xD0D6DC);
    private static final Color TRIANGLE_COLOR = new Color(0x168AAD);
    private static final Color SQUARE_COLOR = new Color(0xD1495B);

    private static final int HALF_CYCLES = 4;          // ticks at 1x, 2x, 3x, 4x the entered t
    private static final int MIN_VISIBLE_PX = 6;       // keeps a tiny non-zero value visible
    private static final int LOW_AMP_PX = 20;          // below this, numbers move clear of the trace

    private double inputAmplitude;
    private double outputAmplitude;
    private double halfCycleMs = Double.NaN;
    private boolean hasValues;

    /** Pixel geometry shared by the drawing helpers for one plot. */
    private static final class Geometry {
        int axisX;      // x of the vertical axis
        int axisEnd;    // x where the time axis (arrow) ends
        int dataW;      // pixel width of the plotted time span
        int yTop, yBottom, y0;
        int amp;        // signed pixel height of the +1 level (negative = flipped)

        int x(double halfCycles) {
            return axisX + (int) Math.round(halfCycles / HALF_CYCLES * dataW);
        }

        int y(double level) {
            return y0 - (int) Math.round(level * amp);
        }
    }

    public IntegratorWaveformPanel() {
        setPreferredSize(new Dimension(640, 210));
        setMinimumSize(new Dimension(460, 190));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(0xC7D3DE), 1, true));
    }

    /** Vin and solved Vout only; the time ticks stay unlabeled. */
    public void setValues(double inputAmplitude, double outputAmplitude) {
        setValues(inputAmplitude, outputAmplitude, Double.NaN);
    }

    /**
     * @param inputAmplitude  entered Vin (V)
     * @param outputAmplitude solved Vout (V)
     * @param halfCycleMs     entered t (ms); one half-cycle of the drawn waves
     */
    public void setValues(double inputAmplitude, double outputAmplitude, double halfCycleMs) {
        this.inputAmplitude = inputAmplitude;
        this.outputAmplitude = outputAmplitude;
        this.halfCycleMs = halfCycleMs;
        this.hasValues = true;
        repaint();
    }

    /** Back to the initial state: two flat lines. */
    public void reset() {
        hasValues = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        if (w < 240 || h < 120) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(UITheme.FONT_DIAGRAM);
        FontMetrics fm = g2.getFontMetrics();

        // Anything not finite (e.g. division by zero) is drawn as flat lines.
        boolean valid = hasValues && Double.isFinite(inputAmplitude) && Double.isFinite(outputAmplitude);
        double vin = valid ? inputAmplitude : 0.0;
        double vout = valid ? outputAmplitude : 0.0;
        double largest = Math.max(Math.abs(vin), Math.abs(vout));

        int yTop = 12;
        int yBottom = h - 30;
        int y0 = (yTop + yBottom) / 2;
        int maxAmp = (int) Math.round((yBottom - yTop) / 2.0 * 0.56);
        int inAmp = scaledAmp(vin, largest, maxAmp);
        int outAmp = scaledAmp(vout, largest, maxAmp);

        String[] inLevels = levels(vin, inAmp, "0");
        String[] outLevels = levels(vout, outAmp, "0V");
        String[] tickLabels = tickLabels(valid);

        // Reserve room for the widest level label and the "t (ms)" caption.
        int labelW = 0;
        for (String s : inLevels) if (s != null) labelW = Math.max(labelW, fm.stringWidth(s));
        for (String s : outLevels) if (s != null) labelW = Math.max(labelW, fm.stringWidth(s));
        labelW += 12;
        int tLabelW = fm.stringWidth("t (ms)") + 10;

        int margin = 8;
        int gap = Math.max(64, Math.min(96, w / 8));
        int plotW = (w - 2 * margin - gap) / 2;
        int leftPlot = margin;
        int rightPlot = margin + plotW + gap;

        drawPlot(g2, fm, leftPlot, plotW, labelW, tLabelW, yTop, yBottom, y0, inAmp,
                "Vin", inLevels, tickLabels, true, "Triangle wave");
        drawPlot(g2, fm, rightPlot, plotW, labelW, tLabelW, yTop, yBottom, y0, outAmp,
                "Vo", outLevels, tickLabels, false, "Square wave");

        // Integral arrow between the two plots (at axis height).
        int gapLeft = leftPlot + plotW;
        int gapRight = rightPlot;
        int cx = (gapLeft + gapRight) / 2;
        g2.setColor(UITheme.WIRE);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawLine(gapLeft + 10, y0, gapRight - 12, y0);
        arrowHead(g2, gapRight - 8, y0, 0);
        drawIntegralSign(g2, cx, y0 - 24);

        g2.dispose();
    }

    // ---------------------------------------------------------------
    // One plot: axes, grid, ticks, waveform, labels, caption
    // ---------------------------------------------------------------

    private void drawPlot(Graphics2D g2, FontMetrics fm, int left, int plotW,
                          int labelW, int tLabelW, int yTop, int yBottom, int y0, int amp,
                          String axisTitle, String[] levels, String[] tickLabels,
                          boolean triangle, String caption) {
        Geometry geo = new Geometry();
        geo.axisX = left + labelW;
        geo.axisEnd = left + plotW - tLabelW;
        geo.dataW = geo.axisEnd - geo.axisX - 16;
        geo.yTop = yTop;
        geo.yBottom = yBottom;
        geo.y0 = y0;
        geo.amp = amp;

        int gridEnd = geo.x(HALF_CYCLES) + 8;

        // Light guide lines at the two peak levels (only when there is a swing).
        if (amp != 0) {
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(GRID);
            g2.drawLine(geo.axisX, geo.y(1), gridEnd, geo.y(1));
            g2.drawLine(geo.axisX, geo.y(-1), gridEnd, geo.y(-1));
        }

        // Axes with arrowheads.
        g2.setColor(UITheme.WIRE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(geo.axisX, yBottom, geo.axisX, yTop + 6);
        arrowHead(g2, geo.axisX, yTop, -Math.PI / 2);
        g2.drawLine(geo.axisX, y0, geo.axisEnd - 6, y0);
        arrowHead(g2, geo.axisEnd, y0, 0);

        // Waveform (a flat line along the axis while amp == 0).
        Color waveColor = triangle ? TRIANGLE_COLOR : SQUARE_COLOR;
        g2.setColor(waveColor);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f));
        g2.draw(triangle ? trianglePath(geo) : squarePath(geo));

        // Axis title next to the arrow tip.
        g2.setColor(UITheme.LABEL_TEXT);
        g2.setFont(UITheme.FONT_LABEL_B);
        g2.drawString(axisTitle, geo.axisX + 8, yTop + 9);

        // Level labels (right-aligned against the vertical axis).
        g2.setFont(UITheme.FONT_DIAGRAM);
        int textDrop = (fm.getAscent() - fm.getDescent()) / 2;
        if (levels[0] != null) drawRight(g2, fm, levels[0], geo.axisX - 6, geo.y(1) + textDrop);
        if (levels[1] != null) drawRight(g2, fm, levels[1], geo.axisX - 6, y0 + textDrop);
        if (levels[2] != null) drawRight(g2, fm, levels[2], geo.axisX - 6, geo.y(-1) + textDrop);

        // Time-axis label.
        g2.drawString("t (ms)", geo.axisEnd + 5, y0 + textDrop);

        // Time ticks (+ numbers once t is known).
        int ampAbs = Math.abs(amp);
        boolean lowAmp = ampAbs < LOW_AMP_PX;
        g2.setStroke(new BasicStroke(1.2f));

        // Long numbers (big t): label every second tick so they never run together.
        boolean thinLabels = false;
        if (tickLabels != null) {
            int widest = 0;
            for (String s : tickLabels) widest = Math.max(widest, fm.stringWidth(s));
            thinLabels = widest + 10 > geo.x(1) - geo.x(0);
        }

        for (int i = 0; i < HALF_CYCLES; i++) {
            int tx = geo.x(i + 1);
            g2.setColor(UITheme.WIRE);
            g2.drawLine(tx, y0 - 3, tx, y0 + 3);
            if (tickLabels == null || (thinLabels && i % 2 == 0)) continue;

            String text = tickLabels[i];
            int lw = fm.stringWidth(text);
            int lx;
            int ly;
            int below = y0 + (lowAmp ? ampAbs + 4 : 3) + fm.getAscent();
            if (triangle) {
                // Peaks sit on the ticks: put the number on the empty side of
                // the axis so the line never runs through it.
                double shape = (i % 2 == 0) ? -1 : 1;
                boolean waveBelowAxis = amp * shape < 0;
                lx = tx - lw / 2;
                ly = (!lowAmp && waveBelowAxis) ? y0 - 7 : below;
            } else {
                // Square edges sit on the ticks: keep the number right of the edge.
                lx = tx + 4;
                ly = below;
            }
            g2.setColor(UITheme.LABEL_TEXT);
            g2.drawString(text, lx, ly);
        }

        // Caption under the plot, tinted like its trace.
        g2.setFont(UITheme.FONT_LABEL_B);
        FontMetrics cfm = g2.getFontMetrics();
        g2.setColor(waveColor);
        g2.drawString(caption, left + (plotW - cfm.stringWidth(caption)) / 2, getHeight() - 10);
        g2.setFont(UITheme.FONT_DIAGRAM);
    }

    /** Triangle: peaks land on every tick, +level at 0/2/4 and -level at 1/3 half-cycles. */
    private Path2D trianglePath(Geometry geo) {
        Path2D path = new Path2D.Double();
        path.moveTo(geo.x(0), geo.y(1));
        path.lineTo(geo.x(1), geo.y(-1));
        path.lineTo(geo.x(2), geo.y(1));
        path.lineTo(geo.x(3), geo.y(-1));
        path.lineTo(geo.x(4), geo.y(1));
        return path;
    }

    /** Square: +level for the first half-cycle, -level for the next, edges on every tick. */
    private Path2D squarePath(Geometry geo) {
        Path2D path = new Path2D.Double();
        path.moveTo(geo.x(0), geo.y(1));
        path.lineTo(geo.x(1), geo.y(1));
        path.lineTo(geo.x(1), geo.y(-1));
        path.lineTo(geo.x(2), geo.y(-1));
        path.lineTo(geo.x(2), geo.y(1));
        path.lineTo(geo.x(3), geo.y(1));
        path.lineTo(geo.x(3), geo.y(-1));
        path.lineTo(geo.x(4), geo.y(-1));
        path.lineTo(geo.x(4), geo.y(1));
        return path;
    }

    // ---------------------------------------------------------------
    // Scaling, labels and small drawing helpers
    // ---------------------------------------------------------------

    /** Signed pixel height for {@code value} on the shared scale (0 for a zero value). */
    private int scaledAmp(double value, double largest, int maxAmp) {
        if (value == 0 || largest == 0) {
            return 0;
        }
        int px = (int) Math.max(MIN_VISIBLE_PX, Math.round(maxAmp * Math.abs(value) / largest));
        return value < 0 ? -px : px;
    }

    /**
     * {top, zero, bottom} level labels for one plot; a null entry is not drawn.
     * "top" sits at the +1 level of the trace, so a negative value puts its
     * (negative) label below the axis, matching the flipped trace.
     */
    private String[] levels(double value, int ampPx, String zeroLabel) {
        if (value == 0) {
            return new String[]{null, zeroLabel, null};
        }
        String top = VOLT_FMT.format(value) + "V";
        String bottom = VOLT_FMT.format(-value) + "V";
        // Drop the zero label when it would collide with the peak labels.
        String zero = Math.abs(ampPx) < 12 ? null : zeroLabel;
        return new String[]{top, zero, bottom};
    }

    /** Tick numbers = entered t times 1..4, or null when there is no usable t yet. */
    private String[] tickLabels(boolean valid) {
        if (!valid || !Double.isFinite(halfCycleMs) || halfCycleMs <= 0) {
            return null;
        }
        String[] labels = new String[HALF_CYCLES];
        for (int i = 0; i < HALF_CYCLES; i++) {
            labels[i] = TIME_FMT.format(halfCycleMs * (i + 1));
        }
        return labels;
    }

    private void drawRight(Graphics2D g2, FontMetrics fm, String text, int rightX, int baseline) {
        g2.drawString(text, rightX - fm.stringWidth(text), baseline);
    }

    /** Filled arrowhead whose tip is at (x, y), pointing along {@code angle} (radians). */
    private void arrowHead(Graphics2D g2, double x, double y, double angle) {
        double len = 9;
        double spread = Math.toRadians(24);
        Path2D head = new Path2D.Double();
        head.moveTo(x, y);
        head.lineTo(x - len * Math.cos(angle - spread), y - len * Math.sin(angle - spread));
        head.lineTo(x - len * Math.cos(angle + spread), y - len * Math.sin(angle + spread));
        head.closePath();
        g2.fill(head);
    }

    /** Hand-drawn style integral sign, roughly 30 px tall, centred on (cx, cy). */
    private void drawIntegralSign(Graphics2D g2, double cx, double cy) {
        Path2D p = new Path2D.Double();
        p.moveTo(cx - 6, cy + 11);
        p.curveTo(cx - 5, cy + 16, cx - 2, cy + 16, cx - 2, cy + 9);
        p.lineTo(cx + 2, cy - 9);
        p.curveTo(cx + 2, cy - 16, cx + 5, cy - 16, cx + 6, cy - 11);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(p);
    }
}
package opampapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Shows the circuit diagram, the labeled input fields, a Calculate
 * button and the resulting output for a single {@link CircuitType}.
 */
public class OpAmpPanel extends JPanel {

    private final CircuitType type;
    private final JTextField[] fields;
    private final JLabel resultLabel;
    private final IntegratorWaveformPanel waveformPanel;
    private static final DecimalFormat DF = new DecimalFormat("0.###");

    public OpAmpPanel(CircuitType type) {
        this.type = type;
        setBackground(UITheme.CONTENT_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        setLayout(new BorderLayout(0, 16));

        // ----- Title -----
        JLabel title = new JLabel(type.title);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.ACCENT_NAVY);
        add(title, BorderLayout.NORTH);

        // ----- Center: diagram (top) + form (bottom) -----
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        CircuitDiagramPanel diagram = new CircuitDiagramPanel(type);
        diagram.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(diagram);
        center.add(Box.createVerticalStrut(18));

        waveformPanel = type == CircuitType.INTEGRATOR ? new IntegratorWaveformPanel() : null;
        if (waveformPanel != null) {
            waveformPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            center.add(waveformPanel);
            center.add(Box.createVerticalStrut(18));
        }

        JLabel formulaLabel = new JLabel(formattedFormula(type));
        formulaLabel.setForeground(new Color(0x50606E));
        formulaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(formulaLabel);
        center.add(Box.createVerticalStrut(14));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 10);
        gc.anchor = GridBagConstraints.WEST;

        fields = new JTextField[type.inputLabels.length];
        for (int i = 0; i < type.inputLabels.length; i++) {
            gc.gridx = 0;
            gc.gridy = i;
            JLabel l = new JLabel(type.inputLabels[i] + ":");
            l.setFont(UITheme.FONT_LABEL);
            l.setForeground(UITheme.LABEL_TEXT);
            form.add(l, gc);

            gc.gridx = 1;
            JTextField tf = new JTextField(10);
            tf.setFont(UITheme.FONT_LABEL);
            fields[i] = tf;
            form.add(tf, gc);
        }

        gc.gridx = 2;
        gc.gridy = 0;
        gc.gridheight = Math.max(1, type.inputLabels.length);
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(5, 20, 5, 10);
        StyledButton calcBtn = new StyledButton("Calculate");
        calcBtn.setPreferredSize(new Dimension(120, 40));
        calcBtn.addActionListener(e -> calculate());
        form.add(calcBtn, gc);

        center.add(form);
        add(center, BorderLayout.CENTER);

        // ----- Result -----
        resultLabel = new JLabel(" ");
        resultLabel.setFont(UITheme.FONT_RESULT);
        resultLabel.setForeground(UITheme.ACCENT_NAVY);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setOpaque(false);
        south.add(resultLabel);
        add(south, BorderLayout.SOUTH);
    }

    private void calculate() {
        double[] values = new double[fields.length];
        for (int i = 0; i < fields.length; i++) {
            String txt = fields[i].getText().trim();
            try {
                values[i] = Double.parseDouble(txt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for \"" + type.inputLabels[i] + "\".",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                resultLabel.setText(" ");
                if (waveformPanel != null) {
                    waveformPanel.reset();
                }
                return;
            }
        }
        try {
            double result = type.compute(values);
            resultLabel.setText(type.resultName + " = " + DF.format(result) + " " + type.resultUnit);
            if (waveformPanel != null) {
                waveformPanel.setValues(values[2], result, values[3]);
            }
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(this,
                    "Calculation error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String formattedFormula(CircuitType type) {
        String fractionRfRin = fraction("R<sub>f</sub>", "R<sub>in</sub>");
        String fractionRfR1 = fraction("R<sub>f</sub>", "R<sub>1</sub>");

        switch (type) {
            case INVERTING:
                return html("V<sub>out</sub> = -" + fractionRfRin + " × V<sub>in</sub>");
            case NON_INVERTING:
                return html("V<sub>out</sub> = (1 + " + fractionRfR1 + ") × V<sub>in</sub>");
            case DIFFERENTIAL:
                return html("V<sub>out</sub> = "
                    + fraction("R<sub>1</sub> + R<sub>f</sub>", "R<sub>1</sub>") + " × "
                    + fraction("R<sub>3</sub>", "R<sub>2</sub> + R<sub>3</sub>")
                    + " × V<sub>2</sub> − " + fraction("R<sub>f</sub>", "R<sub>1</sub>")
                    + " × V<sub>1</sub>");
            case INTEGRATOR:
                return html("V<sub>out</sub>(t) = V<sub>out</sub>(0) − "
                        + fraction("1", "R × C") + " ∫ V<sub>in</sub>(t) dt"
                        + "<br><span style='font-size:10px'>For constant V<sub>in</sub> over time t</span>");
            case I_TO_V:
                return html("V<sub>out</sub> = −I<sub>in</sub> × R<sub>f</sub>");
            case V_TO_I:
                return html("I<sub>out</sub> = " + fraction("V<sub>in</sub>", "R<sub>1</sub>"));
            default:
                throw new IllegalStateException("Unhandled circuit type: " + type);
        }
    }

    private static String html(String content) {
        return "<html><div style='font-family:Segoe UI;font-size:13px;white-space:nowrap'>"
            + content + "</div></html>";
    }

    private static String fraction(String numerator, String denominator) {
        return "<span style='white-space:nowrap'><sup>" + numerator + "</sup>&frasl;<sub>"
            + denominator + "</sub></span>";
    }
}
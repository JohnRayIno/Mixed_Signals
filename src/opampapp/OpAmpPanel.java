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

        JLabel formulaLabel = new JLabel(type.formula);
        formulaLabel.setFont(UITheme.FONT_LABEL);
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
                return;
            }
        }
        try {
            double result = type.compute(values);
            resultLabel.setText(type.resultName + " = " + DF.format(result) + " " + type.resultUnit);
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(this,
                    "Calculation error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

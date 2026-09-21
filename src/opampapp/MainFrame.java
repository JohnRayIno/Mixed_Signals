package opampapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Top-level window: a light sidebar of application buttons on the
 * left (mirroring the reference UI) and a white CardLayout content
 * area on the right that swaps between the six op-amp panels.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<CircuitType, StyledButton> menuButtons = new LinkedHashMap<>();

    public MainFrame() {
        super("Op-Amp Application Solver — ECPE304 Midterm Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        selectType(CircuitType.INVERTING);

        pack();
        setLocationRelativeTo(null);
    }

    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.SIDEBAR_BORDER),
                new EmptyBorder(18, 16, 18, 16)));
        sidebar.setPreferredSize(new Dimension(240, 620));

        JLabel title = new JLabel("<html><div style='text-align:center;'>OP-AMP<br/>APPLICATION SOLVER</div></html>");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.ACCENT_NAVY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("Choose circuit setup:");
        subtitle.setFont(UITheme.FONT_LABEL);
        subtitle.setForeground(new Color(0x5B6B78));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(16));

        for (CircuitType type : CircuitType.values()) {
            StyledButton btn = new StyledButton("<html><div style='text-align:center;'>" + type.menuLabel + "</div></html>");
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(210, 66));
            btn.setPreferredSize(new Dimension(210, 66));
            btn.addActionListener(e -> selectType(type));
            menuButtons.put(type, btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(14));
        }

        sidebar.add(Box.createVerticalGlue());
        JLabel footer = new JLabel("<html><div style='text-align:center;font-size:10px;'>ECPE304 &middot; Midterm Project</div></html>");
        footer.setForeground(new Color(0x8AA0B3));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(footer);

        return sidebar;
    }

    private JComponent buildContent() {
        contentPanel.setBackground(UITheme.CONTENT_BG);
        contentPanel.setPreferredSize(new Dimension(760, 760));
        for (CircuitType type : CircuitType.values()) {
            contentPanel.add(new OpAmpPanel(type), type.name());
        }
        return contentPanel;
    }

    private void selectType(CircuitType type) {
        cardLayout.show(contentPanel, type.name());
        for (Map.Entry<CircuitType, StyledButton> entry : menuButtons.entrySet()) {
            entry.getValue().setSelectedState(entry.getKey() == type);
        }
    }
}

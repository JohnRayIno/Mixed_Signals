package opampapp;

import java.awt.*;

/**
 * Centralized color / font constants so every panel and button
 * shares the exact same look and feel.
 */
public final class UITheme {

    private UITheme() {}

    // Sidebar
    public static final Color SIDEBAR_BG      = new Color(0xEDEFF2);
    public static final Color SIDEBAR_BORDER  = new Color(0x9FB3C8);

    // Content area
    public static final Color CONTENT_BG      = Color.WHITE;

    // Buttons (light blue "card" gradient, like the reference UI)
    public static final Color BTN_TOP         = new Color(0xF3F8FD);
    public static final Color BTN_BOTTOM      = new Color(0xC7DFF5);
    public static final Color BTN_TOP_HOVER   = new Color(0xE3F0FC);
    public static final Color BTN_BOTTOM_HOVER= new Color(0xAFD1F0);
    public static final Color BTN_TOP_ACTIVE  = new Color(0xBFDCF7);
    public static final Color BTN_BOTTOM_ACTIVE = new Color(0x8FC0EC);
    public static final Color BTN_BORDER      = new Color(0x5B84B1);
    public static final Color BTN_TEXT        = new Color(0x17324D);

    // Header / accent
    public static final Color ACCENT_NAVY     = new Color(0x0B2A52);
    public static final Color LABEL_TEXT      = new Color(0x1B2A38);

    // Diagram
    public static final Color WIRE            = new Color(0x1B2A38);
    public static final Color OPAMP_FILL      = new Color(0xF3F8FD);

    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LABEL    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL_B  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_RESULT   = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_DIAGRAM  = new Font("Segoe UI", Font.PLAIN, 12);
}

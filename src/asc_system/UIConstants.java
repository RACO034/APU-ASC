package asc_system;

import java.awt.Color;
import java.awt.Font;

/**
 * Shared UI constants for the customer module.
 */
public final class UIConstants {
    public static final Color PRIMARY = new Color(100, 181, 246);
    public static final Color PRIMARY_DARK = new Color(25, 118, 210);
    public static final Color ACCENT = new Color(255, 193, 7);
    public static final Color BG_MAIN = new Color(236, 246, 255);
    public static final Color BG_CARD = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(21, 27, 38);
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color ERROR = new Color(239, 68, 68);
    public static final Color BORDER = new Color(186, 219, 255);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.ITALIC, 11);

    private UIConstants() {}
}

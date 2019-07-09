package edu.nju.ics.frontier.scale.view;

import javax.swing.*;
import java.awt.*;

public class ScaleIcon implements Icon {
    private Icon icon;
    private int width;
    private int height;

    public ScaleIcon(Icon icon, int width, int height) {
        this.icon = icon;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        int iconWid = icon.getIconWidth();
        int iconHei = icon.getIconHeight();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.scale(((double) width) / ((double) iconWid), ((double) height) / ((double) iconHei));
        icon.paintIcon(c, g2d, 0, 0);
    }
}

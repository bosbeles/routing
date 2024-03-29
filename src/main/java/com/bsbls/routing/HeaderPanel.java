package com.bsbls.routing;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {


    private static final Font FONT = new Font("TimesRoman", Font.PLAIN, 18);
    private final String left;
    private final String right;

    public HeaderPanel(String left, String right) {
        super();
        this.left = left;
        this.right = right;
        int wl = SwingUtilities.computeStringWidth(this.getFontMetrics(FONT), left);
        int wr = SwingUtilities.computeStringWidth(this.getFontMetrics(FONT), right);
        setMinimumSize(new Dimension(wr + wl, wr + wl));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(FONT);

        // get metrics from the graphics
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // get the height of a line of text in this
        // font and render context
        int hgt = metrics.getHeight() + 2;
        // get the advance of my text in this font
        // and render context

        int fromWidth = metrics.stringWidth(left) + 2;

        int toWidth = metrics.stringWidth(right) + 2;
        // calculate the size of a box to hold the
        // text with some padding.


        Point point = leftTriangle(getWidth(), getHeight() - 8, fromWidth, hgt);
        g2d.drawString(this.left, point.x, point.y + hgt - 4);


        point = rightTriangle(getWidth(), getHeight() - 8, toWidth, hgt);
        g2d.drawString(this.right, point.x, point.y + hgt);


        g2d.setColor(Cell.PRIMARY_COLOR);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(0, 0, this.getWidth(), this.getHeight() - 8);

    }

    private Point rightTriangle(int width, int height, int w, int h) {
        Point p = new Point();
        p.x = (width + (h * width) / height - w) / 2;
        p.y = (height - (height * w) / width - h) / 4;
        return p;
    }

    private Point leftTriangle(int width, int height, int w, int h) {
        Point p = new Point();
        p.x = (width - (h * width) / height - w) / 4;
        p.y = (height + (height * w) / width - h) / 2;
        return p;
    }
}

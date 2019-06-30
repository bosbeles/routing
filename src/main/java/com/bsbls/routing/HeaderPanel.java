package com.bsbls.routing;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class HeaderPanel extends JPanel {


    private final String left;
    private final String right;

    public HeaderPanel(String left, String right)
    {
        super();
        this.left = left;
        this.right = right;
        setPreferredSize(new Dimension(120,120));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("TimesRoman",  Font.PLAIN, 20));

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



        Point point = leftTriangle(getWidth(), getHeight(), fromWidth, hgt);
        g2d.drawString(this.left, point.x, point.y + hgt - 4);


        point = rightTriangle(getWidth(), getHeight(), toWidth, hgt);
        g2d.drawString(this.right, point.x, point.y + hgt);


        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(0,0, this.getWidth(), this.getHeight());

    }

    private Point rightTriangle(int W, int H, int w, int h)
    {
        Point p = new Point();
        p.x = (W + (h*W)/H - w) / 2;
        p.y = (H - (H*w)/W - h) / 4;
        return p;
    }

    private Point leftTriangle(int W, int H, int w, int h)
    {
        Point p = new Point();
        p.x = (W - (h*W)/H - w) / 4;
        p.y = (H + (H*w)/W - h) / 2;
        return p;
    }
}

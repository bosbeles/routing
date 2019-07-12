package com.bsbls.spotlight;

import java.awt.geom.Rectangle2D;

public class Spotlight {
    protected Rectangle2D.Double spot;

    public Spotlight(int x, int y, int w, int h) {
        this.spot = new Rectangle2D.Double(x, y, w, h);
    }

    public Rectangle2D getSpot() {
        return spot;
    }

    public double getArea() {
        return Math.PI * spot.getWidth() * spot.getHeight() / 4.0;
    }
}

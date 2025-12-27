package model;

import java.awt.Rectangle;

public class BanditModel {

    private double x, y;
    private double speed = 1.5;
    private int size = 40;
    private boolean alive = true;

    public BanditModel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveToward(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist == 0) return;

        x += (dx / dist) * speed;
        y += (dy / dist) * speed;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getSize() { return size; }
}

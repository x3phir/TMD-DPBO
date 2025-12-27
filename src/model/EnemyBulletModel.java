package model;

import java.awt.Rectangle;

public class EnemyBulletModel {

    private double x, y;
    private double vx, vy;
    private int size = 6;
    private boolean active = true;

    public EnemyBulletModel(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getSize() { return size; }
}

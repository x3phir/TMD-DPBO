// ==================== BulletModel.java ====================
package model;

import java.awt.Rectangle;

/**
 * BulletModel - Model untuk peluru player
 * Peluru bergerak lurus dengan kecepatan tetap
 */
public class BulletModel {

    // ==================== ATRIBUT ====================
    private double x, y;                    // Posisi peluru
    private final double vx, vy;            // Velocity (kecepatan arah X dan Y)
    private boolean active = true;          // Status aktif/tidak
    
    // ==================== KONSTANTA ====================
    private static final int SIZE = 6;      // Ukuran peluru (6x6 pixel)

    /**
     * Constructor - Buat peluru dengan posisi dan velocity
     */
    public BulletModel(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Update posisi peluru setiap frame
     */
    public void update() {
        x += vx;
        y += vy;
    }

    /**
     * Dapatkan bounding box untuk collision detection
     */
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, SIZE, SIZE);
    }

    // ==================== GETTERS & SETTERS ====================
    
    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public int getX() { 
        return (int)x; 
    }
    
    public int getY() { 
        return (int)y; 
    }
    
    public int getSize() { 
        return SIZE; 
    }
}

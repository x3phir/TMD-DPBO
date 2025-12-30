// ==================== EnemyBulletModel.java ====================
package model;

import java.awt.Rectangle;

/**
 * EnemyBulletModel - Model untuk peluru musuh (bandit)
 * Sama seperti peluru player tapi dengan warna berbeda
 */
public class EnemyBulletModel {

    // ==================== ATRIBUT ====================
    private double x, y;                    // Posisi peluru
    private final double vx, vy;            // Velocity (arah gerak)
    private boolean active = true;          // Status aktif
    
    // ==================== KONSTANTA ====================
    private static final int SIZE = 6;      // Ukuran peluru

    /**
     * Constructor - Buat peluru musuh
     */
    public EnemyBulletModel(double x, double y, double vx, double vy) {
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

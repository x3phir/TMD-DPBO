// ==================== BanditModel.java ====================
package model;

import java.awt.Rectangle;

/**
 * BanditModel - Model untuk musuh bandit
 * Bandit bergerak menuju player dan menembak
 */
public class BanditModel {

    // ==================== ATRIBUT ====================
    private double x, y;                    // Posisi (double untuk smooth movement)
    private boolean alive = true;           // Status hidup/mati
    
    // ==================== KONSTANTA ====================
    private static final double SPEED = 0.5;  // Kecepatan gerak per frame
    private static final int SIZE = 40;       // Ukuran sprite (40x40 pixel)

    /**
     * Constructor - Inisialisasi bandit pada posisi tertentu
     */
    public BanditModel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gerakkan bandit menuju target (player)
     * Menggunakan vector normalization untuk kecepatan konstan
     */
    public void moveToward(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) return;

        // Normalize dan apply speed
        x += (dx / distance) * SPEED;
        y += (dy / distance) * SPEED;
    }

    /**
     * Dapatkan bounding box untuk collision detection
     */
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, SIZE, SIZE);
    }

    // ==================== GETTERS ====================
    
    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
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
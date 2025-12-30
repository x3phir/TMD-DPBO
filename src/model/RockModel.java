// ==================== RockModel.java ====================
package model;

import java.awt.Rectangle;

/**
 * RockModel - Model untuk rintangan batu
 * Batu adalah objek statis yang menghalangi peluru
 */
public class RockModel {

    // ==================== ATRIBUT ====================
    private final int x, y;                 // Posisi (immutable)
    private final int width, height;        // Ukuran (immutable)

    /**
     * Constructor - Buat batu pada posisi dan ukuran tertentu
     */
    public RockModel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Dapatkan bounding box untuk collision detection
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // ==================== GETTERS ====================
    
    public int getX() { 
        return x; 
    }
    
    public int getY() { 
        return y; 
    }
    
    public int getWidth() { 
        return width; 
    }
    
    public int getHeight() { 
        return height; 
    }
}

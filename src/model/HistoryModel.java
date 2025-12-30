// ==================== HistoryModel.java ====================
package model;

/**
 * HistoryModel - Model untuk data history dari database
 * Immutable class untuk data yang sudah tersimpan
 */
public class HistoryModel {

    // ==================== ATRIBUT (FINAL/IMMUTABLE) ====================
    private final String username;
    private final int score;
    private final int ammo;
    private final int bulletsMissed;
    
    /**
     * Constructor - Buat history record
     */
    public HistoryModel(String username, int score, int ammo, int bulletsMissed) {
        this.username = username;
        this.score = score;
        this.ammo = ammo;
        this.bulletsMissed = bulletsMissed;
    }

    // ==================== GETTERS ====================
    
    public String getUsername() { 
        return username; 
    }
    
    public int getScore() { 
        return score; 
    }
    
    public int getAmmo() { 
        return ammo; 
    }
    
    public int getBulletsMissed() { 
        return bulletsMissed; 
    }
}

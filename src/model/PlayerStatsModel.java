// ==================== PlayerStatsModel.java ====================
package model;

/**
 * PlayerStatsModel - Model untuk statistik pemain selama game
 * Tracking shots, misses, dan skor
 */
public class PlayerStatsModel {
    
    // ==================== ATRIBUT ====================
    private final String username;          // Nama pemain (immutable)
    private int score;                      // Skor saat ini
    private int bulletsFired;               // Total peluru ditembakkan
    private int bulletsMissed;              // Total peluru meleset
    private int bulletsRemaining;           // Peluru tersisa
    
    /**
     * Constructor - Inisialisasi stats baru untuk pemain
     */
    public PlayerStatsModel(String username) {
        this.username = username;
        this.score = 0;
        this.bulletsFired = 0;
        this.bulletsMissed = 0;
        this.bulletsRemaining = 0;
    }
    
    // ==================== GETTERS ====================
    
    public String getUsername() {
        return username;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getBulletsFired() {
        return bulletsFired;
    }
    
    public int getBulletsMissed() {
        return bulletsMissed;
    }
    
    public int getBulletsRemaining() {
        return bulletsRemaining;
    }
    
    // ==================== SETTERS & INCREMENTERS ====================
    
    public void setScore(int score) {
        this.score = score;
    }
    
    /**
     * Increment counter peluru ditembakkan
     */
    public void incrementBulletsFired() {
        this.bulletsFired++;
    }
    
    /**
     * Increment counter peluru meleset
     */
    public void incrementBulletsMissed() {
        this.bulletsMissed++;
    }
    
    public void setBulletsRemaining(int bulletsRemaining) {
        this.bulletsRemaining = bulletsRemaining;
    }
}
package model;

/**
 * Model untuk statistik pemain
 */
public class PlayerStatsModel {
    
    private String username;
    private int score;
    private int bulletsFired;
    private int bulletsMissed;
    private int bulletsRemaining;
    
    public PlayerStatsModel(String username) {
        this.username = username;
        this.score = 0;
        this.bulletsFired = 0;
        this.bulletsMissed = 0;
        this.bulletsRemaining = 0;
    }
    
    public String getUsername() {
        return username;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getBulletsFired() {
        return bulletsFired;
    }
    
    public void incrementBulletsFired() {
        this.bulletsFired++;
    }
    
    public int getBulletsMissed() {
        return bulletsMissed;
    }
    
    public void incrementBulletsMissed() {
        this.bulletsMissed++;
    }
    
    public int getBulletsRemaining() {
        return bulletsRemaining;
    }
    
    public void setBulletsRemaining(int bulletsRemaining) {
        this.bulletsRemaining = bulletsRemaining;
    }
}
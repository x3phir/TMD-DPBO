package model;

public class HistoryModel {

    private String username;
    private int score;
    private int ammo;
    private int bulletsMissed;
    
    public HistoryModel(String username, int score, int ammo, int bulletsMissed) {
        this.username = username;
        this.score = score;
        this.ammo = ammo;
        this.bulletsMissed = bulletsMissed;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getAmmo() { return ammo; }
    public int getBulletsMissed() { return bulletsMissed; }
}

package model;

public class HistoryModel {

    private String username;
    private int score;
    private int ammo;

    public HistoryModel(String username, int score, int ammo) {
        this.username = username;
        this.score = score;
        this.ammo = ammo;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getAmmo() { return ammo; }
}

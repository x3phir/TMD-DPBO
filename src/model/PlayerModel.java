package model;

import java.awt.Rectangle;

public class PlayerModel {

    private int x;
    private int y;

    private int healthPoint;
    private int ammo;

    private int maxAmmo;
    private int maxHealth;
    private String username;

    private int score ;


    public PlayerModel(int startX, int startY, String username) {
        this.x = startX;
        this.y = startY;
        this.username = username;
        this.maxAmmo = 50;
        this.maxHealth = 100;
        this.healthPoint = 100;
        this.ammo = 20;
        this.score = 0;
    }

    // posisi
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // HP & ammo
    public int getHp() {
        return healthPoint;
    }

    public int getAmmo() {
        return ammo;
    }

    public int getScore() {
    return score;
    }

    public String getUsername() {
        return username;
    }

    public void addScore(int value) {
        score += value;
    }


    public void takeDamage(int damage) {
        healthPoint -= damage;
        if (healthPoint < 0) healthPoint = 0;
    }

    public void addAmmo(int amount) {
        ammo += amount;
    }

    public int getMaxHp() {
        return maxHealth;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void useAmmo() {
        if (ammo > 0) ammo--;
    }

    public void move(int dx, int dy) {
    x += dx;
    y += dy;

    // Batas layar (sementara hardcode)
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (x > 760) x = 760; // 800 - player width
    if (y > 520) y = 520; // 600 - player height
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 40, 40); // ukuran player 40x40
    }
}
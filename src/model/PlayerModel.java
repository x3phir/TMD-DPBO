package model;

import java.awt.Rectangle;

/**
 * PlayerModel - Model data untuk pemain
 * Menyimpan posisi, HP, ammo, score, dan username
 */
public class PlayerModel {

    // ==================== ATRIBUT POSISI ====================
    private int x;
    private int y;

    // ==================== ATRIBUT STATS ====================
    private int healthPoint;
    private int ammo;
    private int score;
    private String username;

    // ==================== KONSTANTA ====================
    private static final int MAX_AMMO = 50;
    private static final int MAX_HEALTH = 100;
    private static final int PLAYER_SIZE = 40;
    
    // Batas layar (800x600)
    private static final int MIN_X = 0;
    private static final int MIN_Y = 0;
    private static final int MAX_X = 760;  // 800 - 40 (ukuran player)
    private static final int MAX_Y = 560;  // 600 - 40 (ukuran player)

    /**
     * Constructor - Inisialisasi player baru
     * @param startX Posisi awal X
     * @param startY Posisi awal Y
     * @param username Nama pemain
     */
    public PlayerModel(int startX, int startY, String username) {
        this.x = startX;
        this.y = startY;
        this.username = username;
        this.healthPoint = MAX_HEALTH;
        this.ammo = 0;
        this.score = 0;
    }

    // ==================== GETTER POSISI ====================
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // ==================== GETTER STATS ====================
    
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

    public int getMaxHp() {
        return MAX_HEALTH;
    }

    public int getMaxAmmo() {
        return MAX_AMMO;
    }

    // ==================== MANIPULASI STATS ====================
    
    /**
     * Tambah skor pemain
     * @param value Nilai skor yang ditambahkan
     */
    public void addScore(int value) {
        score += value;
    }

    /**
     * Kurangi HP pemain
     * @param damage Jumlah damage yang diterima
     */
    public void takeDamage(int damage) {
        healthPoint -= damage;
        if (healthPoint < 0) {
            healthPoint = 0;
        }
    }

    /**
     * Tambah ammo pemain (maksimal 50)
     * @param amount Jumlah ammo yang ditambahkan
     */
    public void addAmmo(int amount) {
        ammo += amount;
        if (ammo > MAX_AMMO) {
            ammo = MAX_AMMO;
        }
    }

    /**
     * Gunakan 1 ammo untuk menembak
     */
    public void useAmmo() {
        if (ammo > 0) {
            ammo--;
        }
    }

    // ==================== MOVEMENT ====================
    
    /**
     * Gerakkan player dengan delta tertentu
     * Dibatasi oleh batas layar
     * @param dx Perubahan X
     * @param dy Perubahan Y
     */
    public void move(int dx, int dy) {
        x += dx;
        y += dy;

        // Clamp posisi dalam batas layar
        if (x < MIN_X) x = MIN_X;
        if (y < MIN_Y) y = MIN_Y;
        if (x > MAX_X) x = MAX_X;
        if (y > MAX_Y) y = MAX_Y;
    }

    /**
     * Dapatkan bounding box untuk collision detection
     * @return Rectangle yang merepresentasikan area player
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
    }
}
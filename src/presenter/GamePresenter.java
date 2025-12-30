package presenter;

import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

import database.Database;
import model.*;
import util.AudioManager;
import util.CowboyDialog;
import util.GameThread;
import view.GamePanel;
import view.GameView;
import view.MenuView;

/**
 * GamePresenter - Kelas pengendali utama permainan
 * Mengelola logika game, interaksi antar model dan view
 */
public class GamePresenter {
    
    // ==================== KONSTANTA PERMAINAN ====================
    private static final int BANDIT_SPAWN_INTERVAL = 3000;      // Interval spawn bandit (ms)
    private static final int BANDIT_SHOOT_INTERVAL = 1500;      // Interval tembakan bandit (ms)
    private static final int SCREEN_WIDTH = 800;                 // Lebar layar
    private static final int SCREEN_HEIGHT = 600;                // Tinggi layar
    private static final int PLAYER_DAMAGE = 10;                 // Damage per tembakan musuh
    private static final int BANDIT_KILL_SCORE = 100;            // Skor per bunuh bandit
    private static final int AMMO_REWARD_ON_MISS = 1;            // Ammo bonus jika bandit meleset
    private static final int MIN_ROCK_DISTANCE_FROM_PLAYER = 150; // Jarak minimum batu dari player
    
    // ==================== OBJEK PERMAINAN ====================
    private PlayerModel player;
    private final List<BanditModel> bandits = new ArrayList<>();
    private final List<BulletModel> bullets = new ArrayList<>();
    private final List<EnemyBulletModel> enemyBullets = new ArrayList<>();
    private final List<RockModel> rocks = new ArrayList<>();
    private final List<PlayerStatsModel> allPlayersStats = new ArrayList<>();
    
    // ==================== KOMPONEN UI ====================
    private PlayerStatsModel currentStats;
    private final MenuView menuView;
    private final GameView gameView;
    private GameThread gameThread;
    private Timer banditSpawnTimer;
    private Timer banditShootTimer;
    
    // ==================== STATUS PERMAINAN ====================
    private boolean gamePaused = false;
    private boolean isGameOver = false; 

    /**
     * Constructor - Inisialisasi view
     */
    public GamePresenter() {
        this.menuView = new MenuView(this);
        this.gameView = new GameView(this);
    }

    // ==================== GETTER UNTUK VIEW ====================
    
    /** Cek apakah game sedang pause */
    public boolean isGamePaused() {
        return gamePaused;
    }

    /** Ambil statistik pemain saat ini */
    public PlayerStatsModel getCurrentStats() {
        return currentStats;
    }

    /** Tambah statistik pemain ke list */
    public void addPlayerStats(PlayerStatsModel stats) {
        if (stats != null) {
            allPlayersStats.add(stats);
        }
    }

    /** Ambil semua statistik pemain */
    public List<PlayerStatsModel> getAllPlayersStats() {
        return allPlayersStats;
    }

    // ==================== NAVIGASI ANTAR LAYAR ====================
    
    /**
     * Tampilkan menu utama
     * Reset semua state game dan mulai musik menu
     */
    public void showMenu() {
        resetGameState();
        AudioManager.playMusic("menu_music.wav");
        menuView.setVisible(true);
        gameView.setVisible(false);
    }
    
    /**
     * Mulai permainan baru
     * @param username Nama pemain
     */
    public void startGame(String username) {
        String finalName = (username == null || username.trim().isEmpty()) ? "Player1" : username;
        
        resetGameState();
        initializeGame(finalName);
        setupGameView();
        
        // Mulai game loop dan timer
        gameThread = new GameThread(this);
        gameThread.start();
        startTimers();
        
        AudioManager.playMusic("game_music.wav");
    }

    /**
     * Kembali ke menu dari game
     * Simpan progress jika belum game over
     */
    public void returnToMenu() {
        if (!isGameOver && currentStats != null) {
            saveHistory();
        }
        showMenu();
    }

    /**
     * Reset semua state permainan
     * Hentikan timer dan thread
     */
    private void resetGameState() {
        stopAllTimers();
        if (gameThread != null) {
            gameThread.stopGame();
            gameThread = null;
        }
        isGameOver = false;
        gamePaused = false;
        CowboyDialog.clearDialog();
    }
    
    /**
     * Toggle status pause
     * Hentikan/mulai timer dan musik
     */
    public void togglePause() {
        if (isGameOver) return;
        gamePaused = !gamePaused;
        
        if (gamePaused) {
            stopAllTimers();
            AudioManager.stopMusic();
        } else {
            startTimers();
            AudioManager.playMusic("game_music.wav");
        }
        gameView.refresh();
    }

    // ==================== LOGIKA PERMAINAN UTAMA ====================
    
    /**
     * Update game setiap frame (~60 FPS)
     * Dipanggil oleh GameThread
     */
    public void updateGame() {
        if (gamePaused || isGameOver) return;
        
        updateBullets();
        updateEnemyBullets();
        updateBandits();
        checkCollisions();
        checkGameOver();
        
        gameView.refresh();
    }
    
    /**
     * Cek apakah game over (HP habis)
     * Simpan history dan tampilkan layar game over
     */
    private void checkGameOver() {
        if (player.getHp() <= 0 && !isGameOver) {
            isGameOver = true;
            
            currentStats.setBulletsRemaining(player.getAmmo());
            currentStats.setScore(player.getScore());

            SwingUtilities.invokeLater(() -> {
                saveHistory(); 
                endGame();
            });
        }
    }

    /**
     * Akhiri permainan
     * Hentikan semua timer dan tampilkan hasil
     */
    private void endGame() {
        stopAllTimers();
        if (gameThread != null) {
            gameThread.stopGame();
        }
        
        gameView.showGameOverScreen(currentStats);
        showMenu();
    }

    // ==================== DATABASE OPERATIONS ====================
    
    /**
     * Simpan history permainan ke database
     */
    private void saveHistory() {
        if (currentStats == null) return;

        String sql = "INSERT INTO history(username, score, ammo, bullets_missed) VALUES(?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, currentStats.getUsername());
            ps.setInt(2, currentStats.getScore());
            ps.setInt(3, currentStats.getBulletsRemaining());
            ps.setInt(4, currentStats.getBulletsMissed());
            
            ps.executeUpdate();
            System.out.println("History saved: " + currentStats.getUsername());
            
        } catch (Exception e) {
            System.err.println("Error saving history: " + e.getMessage());
        }
    }

    /**
     * Muat history dari database (10 teratas)
     * @return List history pemain
     */
    public List<HistoryModel> loadHistory() {
        List<HistoryModel> list = new ArrayList<>();
        String sql = "SELECT username, score, ammo, bullets_missed FROM history ORDER BY score DESC LIMIT 10";
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new HistoryModel(
                    rs.getString(1), 
                    rs.getInt(2), 
                    rs.getInt(3), 
                    rs.getInt(4)
                ));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // ==================== INISIALISASI GAME ====================

    /**
     * Inisialisasi objek-objek game
     * Buat player, rocks dengan posisi aman
     */
    private void initializeGame(String username) {
        // Bersihkan list objek lama
        bandits.clear();
        bullets.clear();
        enemyBullets.clear();
        rocks.clear();
        
        // Buat player di tengah layar
        int playerSpawnX = 380;
        int playerSpawnY = 260;
        player = new PlayerModel(playerSpawnX, playerSpawnY, username);
        currentStats = new PlayerStatsModel(username);
        
        // Generate 3 batu dengan jarak aman dari player
        generateSafeRocks(playerSpawnX, playerSpawnY);
    }

    /**
     * Generate batu dengan posisi yang aman
     * Tidak terlalu dekat dengan player atau batu lain
     */
    private void generateSafeRocks(int playerX, int playerY) {
        int rocksGenerated = 0;
        int maxAttempts = 50;
        
        while (rocksGenerated < 3) {
            int attempts = 0;
            boolean validPosition = false;
            int rockX = 0, rockY = 0;
            
            // Coba cari posisi valid
            while (!validPosition && attempts < maxAttempts) {
                rockX = 100 + (int)(Math.random() * 600);
                rockY = 150 + (int)(Math.random() * 350);
                
                // Cek jarak dari player
                double distToPlayer = calculateDistance(rockX, rockY, playerX, playerY);
                if (distToPlayer < MIN_ROCK_DISTANCE_FROM_PLAYER) {
                    attempts++;
                    continue;
                }
                
                // Cek jarak dari batu lain
                if (isTooCloseToOtherRocks(rockX, rockY)) {
                    attempts++;
                    continue;
                }
                
                validPosition = true;
            }
            
            // Tambahkan batu atau gunakan posisi fallback
            if (validPosition) {
                rocks.add(new RockModel(rockX, rockY, 64, 64));
            } else {
                int[] pos = getFallbackRockPosition(rocksGenerated);
                rocks.add(new RockModel(pos[0], pos[1], 64, 64));
            }
            rocksGenerated++;
        }
    }

    /**
     * Cek apakah posisi terlalu dekat dengan batu lain
     */
    private boolean isTooCloseToOtherRocks(int x, int y) {
        for (RockModel rock : rocks) {
            double dist = calculateDistance(x, y, rock.getX(), rock.getY());
            if (dist < 100) return true;
        }
        return false;
    }

    /**
     * Hitung jarak Euclidean antara 2 titik
     */
    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Dapatkan posisi fallback untuk batu
     * Posisi aman di pojok layar
     */
    private int[] getFallbackRockPosition(int index) {
        int[][] positions = {
            {100, 450},  // Kiri bawah
            {650, 450},  // Kanan bawah
            {650, 100}   // Kanan atas
        };
        return positions[index % 3];
    }

    /**
     * Setup GameView dengan panel baru
     */
    private void setupGameView() {
        GamePanel panel = new GamePanel(player, bullets, rocks, bandits, enemyBullets);
        panel.setPresenter(this);
        gameView.setGamePanel(panel);
        gameView.setVisible(true);
        menuView.setVisible(false);
        gameView.requestFocusInWindow();
    }

    // ==================== TIMER MANAGEMENT ====================

    /**
     * Mulai timer untuk spawn bandit dan tembakan
     */
    private void startTimers() {
        banditSpawnTimer = new Timer(BANDIT_SPAWN_INTERVAL, e -> spawnBandit());
        banditShootTimer = new Timer(BANDIT_SHOOT_INTERVAL, e -> shootAllBandits());
        banditSpawnTimer.start();
        banditShootTimer.start();
    }

    /**
     * Hentikan semua timer
     */
    private void stopAllTimers() {
        if (banditSpawnTimer != null) banditSpawnTimer.stop();
        if (banditShootTimer != null) banditShootTimer.stop();
    }

    // ==================== KONTROL PLAYER ====================

    /**
     * Gerakkan player berdasarkan input
     * @param dx Perubahan X
     * @param dy Perubahan Y
     */
    public void movePlayer(int dx, int dy) {
        if (gamePaused || isGameOver) return;
        
        int nextX = player.getX() + dx;
        int nextY = player.getY() + dy;
        
        // Cek tabrakan dengan batu
        if (canMoveTo(nextX, nextY)) {
            player.move(dx, dy);
            gameView.refresh();
        }
    }

    /**
     * Player menembak ke arah mouse
     * @param mouseX Posisi X mouse
     * @param mouseY Posisi Y mouse
     */
    public void shoot(int mouseX, int mouseY) {
        if (gamePaused || isGameOver || player.getAmmo() <= 0) return;

        AudioManager.playSoundEffect("shoot.wav");
        currentStats.incrementBulletsFired();
        CowboyDialog.triggerDialog(); 
        
        // Hitung arah peluru
        double dx = mouseX - (player.getX() + 20);
        double dy = mouseY - (player.getY() + 20);
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            double vx = (dx / distance) * 8.0;
            double vy = (dy / distance) * 8.0;
            bullets.add(new BulletModel(player.getX() + 20, player.getY() + 20, vx, vy));
            player.useAmmo();
            currentStats.setBulletsRemaining(player.getAmmo());
        }
    }

    // ==================== UPDATE OBJEK GAME ====================

    /**
     * Update posisi peluru player
     * Hapus jika menabrak batu atau keluar layar
     */
    private void updateBullets() {
        bullets.forEach(BulletModel::update);
        bullets.removeIf(bullet -> {
            boolean hitRock = rocks.stream().anyMatch(r -> bullet.getBounds().intersects(r.getBounds()));
            boolean offScreen = isOffScreen(bullet.getX(), bullet.getY());
            
            // Hitung missed shot
            if (offScreen && !hitRock) {
                currentStats.incrementBulletsMissed();
            }
            
            return hitRock || offScreen || !bullet.isActive();
        });
    }

    /**
     * Update posisi peluru musuh
     * Cek tabrakan dengan player, beri damage
     * Beri reward ammo jika meleset
     */
    private void updateEnemyBullets() {
        enemyBullets.forEach(EnemyBulletModel::update);
        enemyBullets.removeIf(eb -> {
            // Cek tabrakan dengan player
            if (eb.getBounds().intersects(player.getBounds())) {
                player.takeDamage(PLAYER_DAMAGE);
                return true;
            }
            
            // Beri reward jika keluar layar (meleset)
            if (isOffScreen(eb.getX(), eb.getY())) {
                player.addAmmo(AMMO_REWARD_ON_MISS);
                return true;
            }
            
            // Cek tabrakan dengan batu
            return rocks.stream().anyMatch(r -> eb.getBounds().intersects(r.getBounds()));
        });
    }

    /**
     * Update posisi bandit
     * Gerakkan menuju player
     */
    private void updateBandits() {
        bandits.stream()
               .filter(BanditModel::isAlive)
               .forEach(b -> b.moveToward(player.getX(), player.getY()));
    }

    /**
     * Cek tabrakan peluru player dengan bandit
     * Tambah skor jika berhasil membunuh
     */
    private void checkCollisions() {
        for (BulletModel bullet : bullets) {
            for (BanditModel bandit : bandits) {
                if (bandit.isAlive() && bullet.getBounds().intersects(bandit.getBounds())) {
                    bandit.kill();
                    bullet.deactivate();
                    player.addScore(BANDIT_KILL_SCORE);
                    AudioManager.playSoundEffect("bandit_death.wav");
                }
            }
        }
        
        // Hapus bandit yang mati
        bandits.removeIf(b -> !b.isAlive());
    }

    // ==================== HELPER FUNCTIONS ====================

    /**
     * Cek apakah objek keluar layar
     */
    private boolean isOffScreen(int x, int y) {
        return x < -50 || x > SCREEN_WIDTH + 50 || y < -50 || y > SCREEN_HEIGHT + 50;
    }

    /**
     * Cek apakah player bisa pindah ke posisi tertentu
     * Tidak boleh menabrak batu
     */
    private boolean canMoveTo(int x, int y) {
        Rectangle nextBounds = new Rectangle(x, y, 40, 40);
        return rocks.stream().noneMatch(r -> nextBounds.intersects(r.getBounds()));
    }

    /**
     * Spawn bandit baru di bawah layar
     */
    private void spawnBandit() {
        if (gamePaused || isGameOver) return;
        int x = (int) (Math.random() * 700) + 50;
        int y = SCREEN_HEIGHT - 100;
        bandits.add(new BanditModel(x, y));
    }

    /**
     * Semua bandit menembak ke arah player
     */
    private void shootAllBandits() {
        if (gamePaused || isGameOver) return;
        bandits.stream()
               .filter(BanditModel::isAlive)
               .forEach(this::shootFromBandit);
    }

    /**
     * Bandit menembak peluru ke arah player
     */
    private void shootFromBandit(BanditModel bandit) {
        double dx = (player.getX() + 20) - (bandit.getX() + 20);
        double dy = (player.getY() + 20) - (bandit.getY() + 20);
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if (dist > 0) {
            double vx = (dx / dist) * 4;
            double vy = (dy / dist) * 4;
            enemyBullets.add(new EnemyBulletModel(
                bandit.getX() + 20, 
                bandit.getY() + 20, 
                vx, 
                vy
            ));
            AudioManager.playSoundEffect("enemy_shoot.wav");
        }
    }
}
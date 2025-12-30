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

public class GamePresenter {
    
    // Constants
    private static final int BANDIT_SPAWN_INTERVAL = 3000;
    private static final int BANDIT_SHOOT_INTERVAL = 1500;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int PLAYER_DAMAGE = 10;
    private static final int BANDIT_KILL_SCORE = 100;
    private static final int AMMO_REWARD_ON_MISS = 1;
    
    // Game Objects
    private PlayerModel player;
    private final List<BanditModel> bandits = new ArrayList<>();
    private final List<BulletModel> bullets = new ArrayList<>();
    private final List<EnemyBulletModel> enemyBullets = new ArrayList<>();
    private final List<RockModel> rocks = new ArrayList<>();
    private final List<PlayerStatsModel> allPlayersStats = new ArrayList<>();
    
    // State Tracking
    private PlayerStatsModel currentStats;
    private final MenuView menuView;
    private final GameView gameView;
    private GameThread gameThread;
    private Timer banditSpawnTimer;
    private Timer banditShootTimer;
    
    // Flags penting untuk mencegah looping
    private boolean gamePaused = false;
    private boolean isGameOver = false; 

    public GamePresenter() {
        this.menuView = new MenuView(this);
        this.gameView = new GameView(this);
    }

    // ==================== Missing Methods for View Compatibility ====================

    /**
     * Mengecek apakah game sedang dalam kondisi pause.
     * Dibutuhkan oleh GameView dan GamePanel untuk render overlay "PAUSED".
     */
    public boolean isGamePaused() {
        return gamePaused;
    }

    /**
     * Mengambil statistik pemain saat ini.
     * Dibutuhkan oleh GamePanel untuk menampilkan jumlah tembakan meleset (MISSED).
     */
    public PlayerStatsModel getCurrentStats() {
        return currentStats;
    }

    /**
     * Menambahkan statistik pemain ke list riwayat sesi ini.
     * Dibutuhkan oleh MenuView.
     */
    public void addPlayerStats(PlayerStatsModel stats) {
        if (stats != null) {
            allPlayersStats.add(stats);
        }
    }

    /**
     * Mengembalikan list semua statistik pemain dalam sesi ini.
     */
    public List<PlayerStatsModel> getAllPlayersStats() {
        return allPlayersStats;
    }

    /**
     * Aksi untuk kembali ke menu dari tengah permainan.
     * Dibutuhkan oleh GameView (Key Listener).
     */
    public void returnToMenu() {
        if (!isGameOver && currentStats != null) {
            saveHistory(); // Simpan progres sebelum keluar
        }
        showMenu();
    }
    
    // ==================== Navigation Actions ====================
    
    public void showMenu() {
        resetGameState();
        AudioManager.playMusic("menu_music.wav");
        menuView.setVisible(true);
        gameView.setVisible(false);
    }
    
    public void startGame(String username) {
        String finalName = (username == null || username.trim().isEmpty()) ? "Player1" : username;
        
        resetGameState(); // Bersihkan data lama
        initializeGame(finalName);
        setupGameView();
        
        // Mulai sistem game
        gameThread = new GameThread(this);
        gameThread.start();
        startTimers();
        
        AudioManager.playMusic("game_music.wav");
    }

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
    
    public void togglePause() {
        if (isGameOver) return;
        gamePaused = !gamePaused;
        
        if (gamePaused) {
            if (banditSpawnTimer != null) banditSpawnTimer.stop();
            if (banditShootTimer != null) banditShootTimer.stop();
            AudioManager.stopMusic();
        } else {
            if (banditSpawnTimer != null) banditSpawnTimer.start();
            if (banditShootTimer != null) banditShootTimer.start();
            AudioManager.playMusic("game_music.wav");
        }
        gameView.refresh();
    }

    // ==================== Game Logic ====================
    
    public void updateGame() {
        // Kunci utama: Jika pause atau sudah game over, berhenti total.
        if (gamePaused || isGameOver) return;
        
        updateBullets();
        updateEnemyBullets();
        updateBandits();
        checkCollisions();
        checkGameOver();
        
        gameView.refresh();
    }
    
    private void checkGameOver() {
        if (player.getHp() <= 0 && !isGameOver) {
            isGameOver = true; // Langsung kunci agar tidak masuk ke sini lagi
            
            // Simpan status terakhir
            currentStats.setBulletsRemaining(player.getAmmo());
            currentStats.setScore(player.getScore());

            // Jalankan proses penyelesaian di thread UI agar tidak bentrok
            SwingUtilities.invokeLater(() -> {
                saveHistory(); 
                endGame();
            });
        }
    }

    private void endGame() {
        stopAllTimers();
        if (gameThread != null) {
            gameThread.stopGame();
        }
        
        // Tampilkan popup skor
        gameView.showGameOverScreen(currentStats);
        
        // Kembali ke menu setelah dialog ditutup
        showMenu();
    }

    // ==================== Database Operations ====================
    
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
            System.out.println("History saved successfully for " + currentStats.getUsername());
            
        } catch (Exception e) {
            System.err.println("Error saving history: " + e.getMessage());
        }
    }

    // ==================== Internal Helpers ====================

    private void initializeGame(String username) {
        bandits.clear();
        bullets.clear();
        enemyBullets.clear();
        rocks.clear();
        
        player = new PlayerModel(380, 260, username);
        currentStats = new PlayerStatsModel(username);
        
        // Posisi batu
        for (int i = 0; i < 3; i++) {
            int x = 150 + (i * 250) + (int)(Math.random() * 50 - 25);
            int y = 200 + (int)(Math.random() * 200);
            rocks.add(new RockModel(x, y, 64, 64));
        }
    }

    private void setupGameView() {
        GamePanel panel = new GamePanel(player, bullets, rocks, bandits, enemyBullets);
        panel.setPresenter(this);
        gameView.setGamePanel(panel);
        gameView.setVisible(true);
        menuView.setVisible(false);
        gameView.requestFocusInWindow();
    }

    private void startTimers() {
        banditSpawnTimer = new Timer(BANDIT_SPAWN_INTERVAL, e -> spawnBandit());
        banditShootTimer = new Timer(BANDIT_SHOOT_INTERVAL, e -> shootAllBandits());
        banditSpawnTimer.start();
        banditShootTimer.start();
    }

    private void stopAllTimers() {
        if (banditSpawnTimer != null) banditSpawnTimer.stop();
        if (banditShootTimer != null) banditShootTimer.stop();
    }

    // ... (Metode updateBullets, updateEnemyBullets, movePlayer, shoot tetap sama namun tambahkan pengecekan isGameOver)

    public void movePlayer(int dx, int dy) {
        if (gamePaused || isGameOver) return;
        
        int nextX = player.getX() + dx;
        int nextY = player.getY() + dy;
        
        if (canMoveTo(nextX, nextY)) {
            player.move(dx, dy);
            gameView.refresh();
        }
    }

    public void shoot(int mouseX, int mouseY) {
        if (gamePaused || isGameOver || player.getAmmo() <= 0) return;

        AudioManager.playSoundEffect("shoot.wav");
        currentStats.incrementBulletsFired();
        
        // Logika perhitungan peluru (tetap sama)
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

    private void updateBullets() {
        bullets.forEach(BulletModel::update);
        // Cek collision batu & offscreen
        bullets.removeIf(bullet -> {
            boolean hitRock = rocks.stream().anyMatch(r -> bullet.getBounds().intersects(r.getBounds()));
            boolean offScreen = isOffScreen(bullet.getX(), bullet.getY());
            if (offScreen && !hitRock) currentStats.incrementBulletsMissed();
            return hitRock || offScreen || !bullet.isActive();
        });
    }

    private void updateEnemyBullets() {
        enemyBullets.forEach(EnemyBulletModel::update);
        enemyBullets.removeIf(eb -> {
            if (eb.getBounds().intersects(player.getBounds())) {
                player.takeDamage(PLAYER_DAMAGE);
                return true;
            }
            if (isOffScreen(eb.getX(), eb.getY())) {
                player.addAmmo(AMMO_REWARD_ON_MISS);
                return true;
            }
            return rocks.stream().anyMatch(r -> eb.getBounds().intersects(r.getBounds()));
        });
    }

    private void updateBandits() {
        bandits.stream().filter(BanditModel::isAlive)
               .forEach(b -> b.moveToward(player.getX(), player.getY()));
    }

    private void checkCollisions() {
        for (BulletModel b : bullets) {
            for (BanditModel bandit : bandits) {
                if (bandit.isAlive() && b.getBounds().intersects(bandit.getBounds())) {
                    bandit.kill();
                    b.deactivate();
                    player.addScore(BANDIT_KILL_SCORE);
                    AudioManager.playSoundEffect("bandit_death.wav");
                }
            }
        }
        bandits.removeIf(b -> !b.isAlive());
    }

    private boolean isOffScreen(int x, int y) {
        return x < -50 || x > SCREEN_WIDTH + 50 || y < -50 || y > SCREEN_HEIGHT + 50;
    }

    private boolean canMoveTo(int x, int y) {
        Rectangle nextBounds = new Rectangle(x, y, 40, 40);
        return rocks.stream().noneMatch(r -> nextBounds.intersects(r.getBounds()));
    }

    private void spawnBandit() {
        if (gamePaused || isGameOver) return;
        int x = (int) (Math.random() * 700) + 50;
        int y = SCREEN_HEIGHT - 100;
        bandits.add(new BanditModel(x, y));
    }

    private void shootAllBandits() {
        if (gamePaused || isGameOver) return;
        for (BanditModel b : bandits) {
            if (b.isAlive()) shootFromBandit(b);
        }
    }

    private void shootFromBandit(BanditModel bandit) {
        double dx = (player.getX() + 20) - (bandit.getX() + 20);
        double dy = (player.getY() + 20) - (bandit.getY() + 20);
        double dist = Math.sqrt(dx*dx + dy*dy);
        if (dist > 0) {
            enemyBullets.add(new EnemyBulletModel(bandit.getX()+20, bandit.getY()+20, (dx/dist)*4, (dy/dist)*4));
            AudioManager.playSoundEffect("enemy_shoot.wav");
        }
    }

    public List<HistoryModel> loadHistory() {
        List<HistoryModel> list = new ArrayList<>();
        String sql = "SELECT username, score, ammo, bullets_missed FROM history ORDER BY score DESC LIMIT 10";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new HistoryModel(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
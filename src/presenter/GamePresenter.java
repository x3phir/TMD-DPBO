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
    private static final int MIN_ROCK_DISTANCE_FROM_PLAYER = 150; // Jarak minimum batu dari player
    
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

    public boolean isGamePaused() {
        return gamePaused;
    }

    public PlayerStatsModel getCurrentStats() {
        return currentStats;
    }

    public void addPlayerStats(PlayerStatsModel stats) {
        if (stats != null) {
            allPlayersStats.add(stats);
        }
    }

    public List<PlayerStatsModel> getAllPlayersStats() {
        return allPlayersStats;
    }

    public void returnToMenu() {
        if (!isGameOver && currentStats != null) {
            saveHistory();
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
        
        resetGameState();
        initializeGame(finalName);
        setupGameView();
        
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
            isGameOver = true;
            
            currentStats.setBulletsRemaining(player.getAmmo());
            currentStats.setScore(player.getScore());

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
        
        gameView.showGameOverScreen(currentStats);
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
        
        // Posisi spawn player di tengah
        int playerSpawnX = 380;
        int playerSpawnY = 260;
        
        player = new PlayerModel(playerSpawnX, playerSpawnY, username);
        currentStats = new PlayerStatsModel(username);
        
        // Generate batu dengan pengecekan jarak dari player
        int rocksGenerated = 0;
        int maxAttempts = 50; // Maksimal percobaan untuk menghindari infinite loop
        
        while (rocksGenerated < 3) {
            int attempts = 0;
            boolean validPosition = false;
            int rockX = 0, rockY = 0;
            
            while (!validPosition && attempts < maxAttempts) {
                // Generate posisi random
                rockX = 100 + (int)(Math.random() * 600); // Range X: 100-700
                rockY = 150 + (int)(Math.random() * 350); // Range Y: 150-500
                
                // Hitung jarak dari player spawn
                double distance = Math.sqrt(
                    Math.pow(rockX - playerSpawnX, 2) + 
                    Math.pow(rockY - playerSpawnY, 2)
                );
                
                // Cek apakah jarak cukup jauh dari player
                if (distance >= MIN_ROCK_DISTANCE_FROM_PLAYER) {
                    // Cek jarak dari batu lain yang sudah ada (minimal 100 pixel)
                    boolean tooCloseToOtherRocks = false;
                    for (RockModel existingRock : rocks) {
                        double distToRock = Math.sqrt(
                            Math.pow(rockX - existingRock.getX(), 2) + 
                            Math.pow(rockY - existingRock.getY(), 2)
                        );
                        if (distToRock < 100) {
                            tooCloseToOtherRocks = true;
                            break;
                        }
                    }
                    
                    if (!tooCloseToOtherRocks) {
                        validPosition = true;
                    }
                }
                
                attempts++;
            }
            
            // Jika valid, tambahkan batu
            if (validPosition) {
                rocks.add(new RockModel(rockX, rockY, 64, 64));
                rocksGenerated++;
                System.out.println("Rock " + rocksGenerated + " spawned at (" + rockX + ", " + rockY + ")");
            } else {
                // Fallback: spawn di pojok yang aman
                if (rocksGenerated == 0) {
                    rocks.add(new RockModel(100, 450, 64, 64));
                } else if (rocksGenerated == 1) {
                    rocks.add(new RockModel(650, 450, 64, 64));
                } else {
                    rocks.add(new RockModel(650, 100, 64, 64));
                }
                rocksGenerated++;
                System.out.println("Rock " + rocksGenerated + " spawned at fallback position");
            }
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
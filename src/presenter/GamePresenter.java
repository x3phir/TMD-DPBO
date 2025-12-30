package presenter;

import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import database.Database;
import model.BanditModel;
import model.BulletModel;
import model.EnemyBulletModel;
import model.HistoryModel;
import model.PlayerModel;
import model.PlayerStatsModel;
import model.RockModel;
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
    private final List<BanditModel> bandits;
    private final List<BulletModel> bullets;
    private final List<EnemyBulletModel> enemyBullets;
    private final List<RockModel> rocks;
    
    // Stats tracking
    private PlayerStatsModel currentStats;
    private List<PlayerStatsModel> allPlayersStats;
    
    // Views
    private final MenuView menuView;
    private final GameView gameView;
    
    // Game Thread & Timers
    private GameThread gameThread;
    private Timer banditSpawnTimer;
    private Timer banditShootTimer;
    private boolean gamePaused = false;
    
    public GamePresenter() {
        this.bandits = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.enemyBullets = new ArrayList<>();
        this.rocks = new ArrayList<>();
        this.allPlayersStats = new ArrayList<>();
        
        this.menuView = new MenuView(this);
        this.gameView = new GameView(this);
    }
    
    // ==================== Menu Actions ====================
    
    public void showMenu() {
        stopAllTimers();
        gamePaused = false;
        
        CowboyDialog.clearDialog();
        AudioManager.playMusic("menu_music.wav");
        
        menuView.setVisible(true);
        gameView.setVisible(false);
    }
    
    public void startGame(String username) {
        if (username == null || username.trim().isEmpty()) {
            username = "Player1";
        }
        
        gamePaused = false;
        CowboyDialog.clearDialog();
        AudioManager.playMusic("game_music.wav");
        
        initializeGame(username);
        setupGameView();
        startGameLoop();
        startTimers();
    }
    
    public void togglePause() {
        gamePaused = !gamePaused;
        
        if (gamePaused) {
            // Pause game
            if (banditSpawnTimer != null) banditSpawnTimer.stop();
            if (banditShootTimer != null) banditShootTimer.stop();
            AudioManager.stopMusic();
        } else {
            // Resume game
            if (banditSpawnTimer != null) banditSpawnTimer.start();
            if (banditShootTimer != null) banditShootTimer.start();
            AudioManager.playMusic("game_music.wav");
        }
        
        gameView.refresh();
    }
    
    public boolean isGamePaused() {
        return gamePaused;
    }
    
    public void returnToMenu() {
        if (currentStats != null) {
            saveHistory();
        }
        showMenu();
    }
    
    // ==================== Game Initialization ====================
    
    private void initializeGame(String username) {
        bandits.clear();
        bullets.clear();
        enemyBullets.clear();
        rocks.clear();
        
        player = new PlayerModel(380, 260, username);
        currentStats = new PlayerStatsModel(username);
        
        initializeRocks();
        
        System.out.println("Game initialized for: " + username);
    }
    
    private void initializeRocks() {
        // Generate random rock positions
        for (int i = 0; i < 3; i++) {
            int x = 150 + (i * 250) + (int)(Math.random() * 50 - 25);
            int y = 200 + (int)(Math.random() * 200);
            rocks.add(new RockModel(x, y, 64, 64));
        }
    }
    
    private void setupGameView() {
        menuView.setVisible(false);
        
        GamePanel panel = new GamePanel(player, bullets, rocks, bandits, enemyBullets);
        panel.setPresenter(this);
        
        gameView.setGamePanel(panel);
        gameView.setVisible(true);
        gameView.requestFocusInWindow();
    }
    
    // ==================== Game Loop ====================
    
    private void startGameLoop() {
        gameThread = new GameThread(this);
        gameThread.start();
    }
    
    public void updateGame() {
        if (gamePaused) return;
        
        updateBullets();
        updateEnemyBullets();
        updateBandits();
        checkCollisions();
        checkGameOver();
        gameView.refresh();
    }
    
    private void updateBullets() {
        for (BulletModel bullet : bullets) {
            bullet.update();
            
            // Check collision with rocks
            boolean hitRock = false;
            for (RockModel rock : rocks) {
                if (bullet.getBounds().intersects(rock.getBounds())) {
                    bullet.deactivate();
                    hitRock = true;
                    break;
                }
            }
            
            // Deactivate bullets that go off-screen
            if (isOffScreen(bullet.getX(), bullet.getY())) {
                if (!hitRock) {
                    currentStats.incrementBulletsMissed();
                }
                bullet.deactivate();
            }
        }
        bullets.removeIf(b -> !b.isActive());
    }
    
    private void updateEnemyBullets() {
        for (EnemyBulletModel enemyBullet : enemyBullets) {
            enemyBullet.update();
            
            // Check collision with rocks
            for (RockModel rock : rocks) {
                if (enemyBullet.getBounds().intersects(rock.getBounds())) {
                    enemyBullet.deactivate();
                    break;
                }
            }
            
            // Check player hit
            if (enemyBullet.isActive() && enemyBullet.getBounds().intersects(player.getBounds())) {
                player.takeDamage(PLAYER_DAMAGE);
                enemyBullet.deactivate();
                continue;
            }
            
            // Reward ammo for missed shots
            if (isOffScreen(enemyBullet.getX(), enemyBullet.getY())) {
                player.addAmmo(AMMO_REWARD_ON_MISS);
                enemyBullet.deactivate();
            }
        }
        enemyBullets.removeIf(b -> !b.isActive());
    }
    
    private void updateBandits() {
        for (BanditModel bandit : bandits) {
            if (bandit.isAlive()) {
                bandit.moveToward(player.getX(), player.getY());
            }
        }
    }
    
    private void checkCollisions() {
        for (BulletModel bullet : bullets) {
            if (!bullet.isActive()) continue;
            
            for (BanditModel bandit : bandits) {
                if (!bandit.isAlive()) continue;
                
                if (bullet.getBounds().intersects(bandit.getBounds())) {
                    bandit.kill();
                    bullet.deactivate();
                    player.addScore(BANDIT_KILL_SCORE);
                    currentStats.setScore(player.getScore());
                    
                    AudioManager.playSoundEffect("bandit_death.wav");
                    CowboyDialog.triggerDialogForced();
                    
                    break;
                }
            }
        }
        
        bandits.removeIf(b -> !b.isAlive());
        bullets.removeIf(b -> !b.isActive());
    }
    
    private void checkGameOver() {
        if (player.getHp() <= 0) {
            currentStats.setBulletsRemaining(player.getAmmo());
            saveHistory();
            endGame();
        }
    }
    
    private boolean isOffScreen(int x, int y) {
        return x < -50 || x > SCREEN_WIDTH + 50 || y < -50 || y > SCREEN_HEIGHT + 50;
    }
    
    // ==================== Player Actions ====================
    
    public void movePlayer(int dx, int dy) {
        if (gamePaused) return;
        
        int nextX = player.getX() + dx;
        int nextY = player.getY() + dy;
        
        if (canMoveTo(nextX, nextY)) {
            player.move(dx, dy);
            gameView.refresh();
        }
    }
    
    private boolean canMoveTo(int x, int y) {
        Rectangle nextBounds = new Rectangle(x, y, 40, 40);
        
        for (RockModel rock : rocks) {
            if (nextBounds.intersects(rock.getBounds())) {
                return false;
            }
        }
        return true;
    }
    
    public void shoot(int mouseX, int mouseY) {
        if (gamePaused) return;
        
        if (player.getAmmo() <= 0) {
            System.out.println("Out of ammo!");
            return;
        }
        
        AudioManager.playSoundEffect("shoot.wav");
        CowboyDialog.triggerDialog();
        
        currentStats.incrementBulletsFired();
        
        int playerCenterX = player.getX() + 20;
        int playerCenterY = player.getY() + 20;
        
        double dx = mouseX - playerCenterX;
        double dy = mouseY - playerCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance == 0) return;
        
        double bulletSpeed = 8.0;
        double vx = (dx / distance) * bulletSpeed;
        double vy = (dy / distance) * bulletSpeed;
        
        bullets.add(new BulletModel(playerCenterX, playerCenterY, vx, vy));
        player.useAmmo();
        currentStats.setBulletsRemaining(player.getAmmo());
        
        gameView.refresh();
    }
    
    // ==================== Enemy Actions ====================
    
    private void startTimers() {
        banditSpawnTimer = new Timer(BANDIT_SPAWN_INTERVAL, e -> spawnBandit());
        banditSpawnTimer.start();
        
        banditShootTimer = new Timer(BANDIT_SHOOT_INTERVAL, e -> shootAllBandits());
        banditShootTimer.start();
    }
    
    private void stopAllTimers() {
        if (banditSpawnTimer != null) {
            banditSpawnTimer.stop();
            banditSpawnTimer = null;
        }
        if (banditShootTimer != null) {
            banditShootTimer.stop();
            banditShootTimer = null;
        }
    }
    
    private void spawnBandit() {
        if (gamePaused) return;
        
        // Spawn from bottom (y = near SCREEN_HEIGHT)
        int x = (int) (Math.random() * 700) + 50;
        int y = SCREEN_HEIGHT - 100 + (int) (Math.random() * 50); // Bottom area
        
        bandits.add(new BanditModel(x, y));
    }
    
    private void shootAllBandits() {
        if (gamePaused) return;
        
        for (BanditModel bandit : bandits) {
            if (bandit.isAlive()) {
                shootFromBandit(bandit);
            }
        }
    }
    
    private void shootFromBandit(BanditModel bandit) {
        AudioManager.playSoundEffect("enemy_shoot.wav");
        
        double banditCenterX = bandit.getX() + 20;
        double banditCenterY = bandit.getY() + 20;
        
        double playerCenterX = player.getX() + 20;
        double playerCenterY = player.getY() + 20;
        
        double dx = playerCenterX - banditCenterX;
        double dy = playerCenterY - banditCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance == 0) return;
        
        double bulletSpeed = 4.0;
        double vx = (dx / distance) * bulletSpeed;
        double vy = (dy / distance) * bulletSpeed;
        
        enemyBullets.add(new EnemyBulletModel(banditCenterX, banditCenterY, vx, vy));
    }
    
    // ==================== Game End ====================
    
    private void endGame() {
        stopAllTimers();
        
        if (gameThread != null) {
            gameThread.stopGame();
            gameThread = null;
        }
        
        CowboyDialog.clearDialog();
        
        gameView.setVisible(false);
        showGameOverDialog();
        showMenu();
    }
    
    private void showGameOverDialog() {
        gameView.showGameOverScreen(currentStats);
    }
    
    // ==================== Database Operations ====================
    
    private void saveHistory() {
        String sql = "INSERT INTO history(username, score, ammo, bullets_missed) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                System.err.println("Failed to save history: Database connection is null");
                return;
            }
            
            ps.setString(1, currentStats.getUsername());
            ps.setInt(2, currentStats.getScore());
            ps.setInt(3, currentStats.getBulletsRemaining());
            ps.setInt(4, currentStats.getBulletsMissed());
            
            ps.executeUpdate();
            System.out.println("History saved for: " + currentStats.getUsername() + 
                             " | Score: " + currentStats.getScore() + 
                             " | Missed: " + currentStats.getBulletsMissed());
            
        } catch (Exception e) {
            System.err.println("Error saving history:");
            e.printStackTrace();
        }
    }
    
    public List<HistoryModel> loadHistory() {
        List<HistoryModel> historyList = new ArrayList<>();
        String sql = "SELECT username, score, ammo, bullets_missed FROM history ORDER BY score DESC LIMIT 10";
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (conn == null) {
                System.err.println("Failed to load history: Database connection is null");
                return historyList;
            }
            
            while (rs.next()) {
                historyList.add(new HistoryModel(
                    rs.getString("username"),
                    rs.getInt("score"),
                    rs.getInt("ammo"),
                    rs.getInt("bullets_missed")
                ));
            }
            
            System.out.println("Loaded " + historyList.size() + " history records");
            
        } catch (Exception e) {
            System.err.println("Error loading history:");
            e.printStackTrace();
        }
        
        return historyList;
    }
    
    public PlayerStatsModel getCurrentStats() {
        return currentStats;
    }
    
    public void addPlayerStats(PlayerStatsModel stats) {
        allPlayersStats.add(stats);
    }
    
    public List<PlayerStatsModel> getAllPlayersStats() {
        return allPlayersStats;
    }
}
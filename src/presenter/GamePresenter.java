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
import model.RockModel;
import util.GameThread;
import view.GamePanel;
import view.GameView;
import view.MenuView;

public class GamePresenter {
    
    // Constants
    private static final int BANDIT_SPAWN_INTERVAL = 3000; // 3 seconds
    private static final int BANDIT_SHOOT_INTERVAL = 1500; // 1.5 seconds
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
    
    // Views
    private final MenuView menuView;
    private final GameView gameView;
    
    // Game Thread & Timers
    private GameThread gameThread;
    private Timer banditSpawnTimer;
    private Timer banditShootTimer;
    
    public GamePresenter() {
        this.bandits = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.enemyBullets = new ArrayList<>();
        this.rocks = new ArrayList<>();
        
        this.menuView = new MenuView(this);
        this.gameView = new GameView(this);
    }
    
    // ==================== Menu Actions ====================
    
    public void showMenu() {
        stopAllTimers();
        menuView.setVisible(true);
        gameView.setVisible(false);
    }
    
    public void startGame(String username) {
        if (username == null || username.trim().isEmpty()) {
            username = "Player1";
        }
        initializeGame(username);
        setupGameView();
        startGameLoop();
        startTimers();
    }
    
    // ==================== Game Initialization ====================
    
    private void initializeGame(String username) {
        // Clear previous game state
        bandits.clear();
        bullets.clear();
        enemyBullets.clear();
        rocks.clear();
        
        // Initialize player
        player = new PlayerModel(380, 260, username);
        
        // Initialize rocks
        initializeRocks();
        
        System.out.println("Game initialized for: " + username + " with " + rocks.size() + " rocks");
    }
    
    private void initializeRocks() {
        rocks.add(new RockModel(200, 150, 64, 64));
        rocks.add(new RockModel(400, 300, 64, 64));
        rocks.add(new RockModel(600, 200, 64, 64));
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
            
            // Deactivate bullets that go off-screen
            if (isOffScreen(bullet.getX(), bullet.getY())) {
                bullet.deactivate();
            }
        }
        bullets.removeIf(b -> !b.isActive());
    }
    
    private void updateEnemyBullets() {
        for (EnemyBulletModel enemyBullet : enemyBullets) {
            enemyBullet.update();
            
            // Check player hit
            if (enemyBullet.getBounds().intersects(player.getBounds())) {
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
        // Bullet hits bandit
        for (BulletModel bullet : bullets) {
            if (!bullet.isActive()) continue;
            
            for (BanditModel bandit : bandits) {
                if (!bandit.isAlive()) continue;
                
                if (bullet.getBounds().intersects(bandit.getBounds())) {
                    bandit.kill();
                    bullet.deactivate();
                    player.addScore(BANDIT_KILL_SCORE);
                    break;
                }
            }
        }
        
        // Remove dead bandits and inactive bullets
        bandits.removeIf(b -> !b.isAlive());
        bullets.removeIf(b -> !b.isActive());
    }
    
    private void checkGameOver() {
        if (player.getHp() <= 0) {
            saveHistory();
            endGame();
        }
    }
    
    private boolean isOffScreen(int x, int y) {
        return x < 0 || x > SCREEN_WIDTH || y < 0 || y > SCREEN_HEIGHT;
    }
    
    // ==================== Player Actions ====================
    
    public void movePlayer(int dx, int dy) {
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
        if (player.getAmmo() <= 0) {
            System.out.println("Out of ammo!");
            return;
        }
        
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
        
        gameView.refresh();
    }
    
    // ==================== Enemy Actions ====================
    
    private void startTimers() {
        // Bandit spawn timer
        banditSpawnTimer = new Timer(BANDIT_SPAWN_INTERVAL, e -> spawnBandit());
        banditSpawnTimer.start();
        
        // Bandit shoot timer
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
        int x = (int) (Math.random() * 700) + 50;
        int y = (int) (Math.random() * 100) + 50;
        
        bandits.add(new BanditModel(x, y));
    }
    
    private void shootAllBandits() {
        for (BanditModel bandit : bandits) {
            if (bandit.isAlive()) {
                shootFromBandit(bandit);
            }
        }
    }
    
    private void shootFromBandit(BanditModel bandit) {
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
        
        gameView.setVisible(false);
        showGameOverDialog();
        showMenu();
    }
    
    private void showGameOverDialog() {
        String message = String.format(
            "Game Over, %s!\n\nFinal Score: %d\nAmmo Left: %d",
            player.getUsername(),
            player.getScore(),
            player.getAmmo()
        );
        
        javax.swing.JOptionPane.showMessageDialog(
            menuView,
            message,
            "Game Over",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // ==================== Database Operations ====================
    
    private void saveHistory() {
        String sql = "INSERT INTO history(username, score, ammo) VALUES(?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                System.err.println("Failed to save history: Database connection is null");
                return;
            }
            
            ps.setString(1, player.getUsername());
            ps.setInt(2, player.getScore());
            ps.setInt(3, player.getAmmo());
            
            ps.executeUpdate();
            System.out.println("History saved for: " + player.getUsername() + " with score: " + player.getScore());
            
        } catch (Exception e) {
            System.err.println("Error saving history:");
            e.printStackTrace();
        }
    }
    
    public List<HistoryModel> loadHistory() {
        List<HistoryModel> historyList = new ArrayList<>();
        String sql = "SELECT username, score, ammo FROM history ORDER BY score DESC LIMIT 10";
        
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
                    rs.getInt("ammo")
                ));
            }
            
            System.out.println("Loaded " + historyList.size() + " history records");
            
        } catch (Exception e) {
            System.err.println("Error loading history:");
            e.printStackTrace();
        }
        
        return historyList;
    }
}
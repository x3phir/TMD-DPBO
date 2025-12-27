package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

import model.BanditModel;
import model.BulletModel;
import model.EnemyBulletModel;
import model.PlayerModel;
import model.RockModel;
import presenter.GamePresenter;
import util.AssetManager;

public class GamePanel extends JPanel {
    
    // Game objects
    private final PlayerModel player;
    private final List<BulletModel> bullets;
    private final List<EnemyBulletModel> enemyBullets;
    private final List<BanditModel> bandits;
    private final List<RockModel> rocks;
    
    // Sprites
    private BufferedImage background;
    private BufferedImage playerSprite;
    private BufferedImage banditSprite;
    private BufferedImage rockSprite;
    private BufferedImage barFrame;
    
    // Presenter
    private GamePresenter presenter;
    
    // UI Colors - Retro Brown Theme
    private static final Color UI_DARK_BROWN = new Color(90, 50, 30);
    private static final Color UI_MED_BROWN = new Color(140, 85, 50);
    private static final Color UI_LIGHT_BROWN = new Color(180, 120, 80);
    private static final Color UI_BORDER = new Color(230, 200, 150);
    private static final Color UI_HIGHLIGHT = new Color(255, 220, 180);
    
    private static final Color HP_BAR_COLOR = new Color(220, 60, 60);
    private static final Color AMMO_BAR_COLOR = new Color(255, 200, 60);
    private static final Color BULLET_COLOR = new Color(255, 215, 0);
    private static final Color ENEMY_BULLET_COLOR = new Color(220, 20, 60);
    
    private static final Font RETRO_FONT = new Font("Monospaced", Font.BOLD, 14);
    
    public GamePanel(PlayerModel player, List<BulletModel> bullets, 
                     List<RockModel> rocks, List<BanditModel> bandits, 
                     List<EnemyBulletModel> enemyBullets) {
        this.player = player;
        this.bullets = bullets;
        this.rocks = rocks;
        this.bandits = bandits;
        this.enemyBullets = enemyBullets;
        
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
        
        loadAssets();
    }
    
    /**
     * Load all game assets using AssetManager
     */
    private void loadAssets() {
        System.out.println("Loading game assets...");
        
        barFrame = AssetManager.loadImage("bar.png");
        background = AssetManager.loadImage("background.png");
        playerSprite = AssetManager.loadImage("player.png");
        banditSprite = AssetManager.loadImage("bandit.png");
        rockSprite = AssetManager.loadImage("rock.png");
        
        System.out.println("All assets loaded successfully!");
    }
    
    /**
     * Set the presenter for handling user input
     */
    public void setPresenter(GamePresenter presenter) {
        this.presenter = presenter;
        
        // Add mouse listener for shooting
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (GamePanel.this.presenter != null) {
                    GamePanel.this.presenter.shoot(e.getX(), e.getY());
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw game elements in order (back to front)
        drawBackground(g2d);
        drawRocks(g2d);
        drawBullets(g2d);
        drawEnemyBullets(g2d);
        drawPlayer(g2d);
        drawBandits(g2d);
        drawUI(g2d);
    }
    
    /**
     * Draw background
     */
    private void drawBackground(Graphics2D g) {
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }
    }
    
    /**
     * Draw player character
     */
    private void drawPlayer(Graphics2D g) {
        if (playerSprite != null) {
            g.drawImage(playerSprite, player.getX(), player.getY(), 40, 40, null);
        } else {
            // Fallback
            g.setColor(Color.BLUE);
            g.fillRect(player.getX(), player.getY(), 40, 40);
        }
    }
    
    /**
     * Draw bandits
     */
    private void drawBandits(Graphics2D g) {
        if (bandits != null) {
            for (BanditModel bandit : bandits) {
                if (bandit.isAlive()) {
                    if (banditSprite != null) {
                        g.drawImage(banditSprite, bandit.getX(), bandit.getY(), 40, 40, null);
                    } else {
                        // Fallback
                        g.setColor(Color.RED);
                        g.fillRect(bandit.getX(), bandit.getY(), 40, 40);
                    }
                }
            }
        }
    }
    
    /**
     * Draw rocks (obstacles)
     */
    private void drawRocks(Graphics2D g) {
        if (rocks != null) {
            for (RockModel rock : rocks) {
                if (rockSprite != null) {
                    g.drawImage(rockSprite, 
                        rock.getX(), rock.getY(), 
                        rock.getWidth(), rock.getHeight(), 
                        null);
                } else {
                    // Fallback
                    g.setColor(Color.GRAY);
                    g.fillRect(rock.getX(), rock.getY(), rock.getWidth(), rock.getHeight());
                }
            }
        }
    }
    
    /**
     * Draw player bullets
     */
    private void drawBullets(Graphics2D g) {
        if (bullets != null) {
            for (BulletModel bullet : bullets) {
                if (bullet.isActive()) {
                    // Bullet glow effect
                    g.setColor(new Color(255, 215, 0, 100));
                    g.fillOval(bullet.getX() - 2, bullet.getY() - 2, 
                              bullet.getSize() + 4, bullet.getSize() + 4);
                    
                    // Bullet
                    g.setColor(BULLET_COLOR);
                    g.fillOval(bullet.getX(), bullet.getY(), 
                              bullet.getSize(), bullet.getSize());
                }
            }
        }
    }
    
    /**
     * Draw enemy bullets
     */
    private void drawEnemyBullets(Graphics2D g) {
        if (enemyBullets != null) {
            for (EnemyBulletModel bullet : enemyBullets) {
                if (bullet.isActive()) {
                    // Bullet glow effect
                    g.setColor(new Color(220, 20, 60, 100));
                    g.fillOval(bullet.getX() - 2, bullet.getY() - 2, 
                              bullet.getSize() + 4, bullet.getSize() + 4);
                    
                    // Bullet
                    g.setColor(ENEMY_BULLET_COLOR);
                    g.fillOval(bullet.getX(), bullet.getY(), 
                              bullet.getSize(), bullet.getSize());
                }
            }
        }
    }
    
    /**
     * Draw UI overlay with Retro 32-bit Brown Theme
     */
    private void drawUI(Graphics2D g) {
        int uiX = 20;
        int uiY = 20;
        
        // ===== HEALTH BAR =====
        drawRetroFrame(g, uiX, uiY, 280, 28);
        drawRetroFill(g, uiX + 6, uiY + 6, 268, 16, 
                      player.getHp(), player.getMaxHp(), HP_BAR_COLOR);
        
        // HP Text
        g.setFont(RETRO_FONT);
        g.setColor(UI_HIGHLIGHT);
        String hpText = "HP: " + player.getHp() + "/" + player.getMaxHp();
        g.drawString(hpText, uiX + 10, uiY + 19);
        
        // ===== AMMO SECTION =====
        int ammoY = uiY + 40;
        
        // Label "Peluru:"
        drawRetroLabelBox(g, uiX, ammoY, 80, 24);
        g.setFont(RETRO_FONT);
        g.setColor(UI_HIGHLIGHT);
        g.drawString("Peluru:", uiX + 8, ammoY + 17);
        
        // Ammo Bar
        int ammoBarX = uiX + 90;
        drawRetroFrame(g, ammoBarX, ammoY, 190, 24);
        drawRetroFill(g, ammoBarX + 6, ammoY + 6, 178, 12, 
                      player.getAmmo(), player.getMaxAmmo(), AMMO_BAR_COLOR);
        
        // Ammo Text
        g.setColor(UI_HIGHLIGHT);
        String ammoText = player.getAmmo() + "/" + player.getMaxAmmo();
        g.drawString(ammoText, ammoBarX + 10, ammoY + 17);
        
        // ===== SCORE =====
        int scoreY = ammoY + 50;
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.setColor(UI_DARK_BROWN);
        g.drawString("SCORE: " + player.getScore(), uiX + 2, scoreY + 2);
        g.setColor(UI_HIGHLIGHT);
        g.drawString("SCORE: " + player.getScore(), uiX, scoreY);
    }
    
    /**
     * Draw retro-style frame with 32-bit pixel art look
     */
    private void drawRetroFrame(Graphics2D g, int x, int y, int w, int h) {
        // Dark brown background
        g.setColor(UI_DARK_BROWN);
        g.fillRect(x, y, w, h);
        
        // Outer light border (top-left highlight)
        g.setColor(UI_BORDER);
        g.fillRect(x, y, w, 2);  // Top
        g.fillRect(x, y, 2, h);  // Left
        
        // Inner medium border
        g.setColor(UI_LIGHT_BROWN);
        g.fillRect(x + 2, y + 2, w - 4, 2);  // Top inner
        g.fillRect(x + 2, y + 2, 2, h - 4);  // Left inner
        
        // Dark shadow (bottom-right)
        g.setColor(new Color(60, 35, 20));
        g.fillRect(x + 2, y + h - 2, w - 2, 2);  // Bottom
        g.fillRect(x + w - 2, y + 2, 2, h - 2);  // Right
        
        // Medium shadow inner
        g.setColor(UI_MED_BROWN);
        g.fillRect(x + 4, y + h - 4, w - 8, 2);  // Bottom inner
        g.fillRect(x + w - 4, y + 4, 2, h - 8);  // Right inner
    }
    
    /**
     * Draw retro label box (for "Peluru:" text)
     */
    private void drawRetroLabelBox(Graphics2D g, int x, int y, int w, int h) {
        // Background
        g.setColor(UI_MED_BROWN);
        g.fillRect(x, y, w, h);
        
        // Highlight
        g.setColor(UI_BORDER);
        g.fillRect(x, y, w, 2);
        g.fillRect(x, y, 2, h);
        
        // Shadow
        g.setColor(UI_DARK_BROWN);
        g.fillRect(x + 2, y + h - 2, w - 2, 2);
        g.fillRect(x + w - 2, y + 2, 2, h - 2);
    }
    
    /**
     * Draw retro-style bar fill with pixel art style
     */
    private void drawRetroFill(Graphics2D g, int x, int y, int w, int h,
                               int current, int max, Color fillColor) {
        float percent = Math.max(0f, Math.min(1f, (float) current / max));
        int fillWidth = (int) (w * percent);
        
        if (fillWidth > 0) {
            // Main fill
            g.setColor(fillColor);
            g.fillRect(x, y, fillWidth, h);
            
            // Top highlight untuk efek 3D
            g.setColor(new Color(255, 255, 255, 80));
            g.fillRect(x, y, fillWidth, h / 3);
            
            // Bottom shadow
            g.setColor(new Color(0, 0, 0, 40));
            g.fillRect(x, y + h - h / 4, fillWidth, h / 4);
            
            // Pixel pattern (opsional untuk efek retro lebih kuat)
            g.setColor(new Color(255, 255, 255, 30));
            for (int i = 0; i < fillWidth; i += 4) {
                g.drawLine(x + i, y, x + i, y + h);
            }
        }
    }
}
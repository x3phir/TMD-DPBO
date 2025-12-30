package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
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
import util.CowboyDialog;

public class GamePanel extends JPanel {
    
    private final PlayerModel player;
    private final List<BulletModel> bullets;
    private final List<EnemyBulletModel> enemyBullets;
    private final List<BanditModel> bandits;
    private final List<RockModel> rocks;
    
    private BufferedImage background;
    private BufferedImage playerSprite;
    private BufferedImage banditSprite;
    private BufferedImage rockSprite;
    private BufferedImage cowboyAvatar;
    
    private GamePresenter presenter;
    
    private static final Color UI_DARK_BROWN = new Color(90, 50, 30);
    private static final Color UI_MED_BROWN = new Color(140, 85, 50);
    private static final Color UI_LIGHT_BROWN = new Color(180, 120, 80);
    private static final Color UI_BORDER = new Color(230, 200, 150);
    private static final Color UI_HIGHLIGHT = new Color(255, 220, 180);
    private static final Color UI_GOLD = new Color(210, 180, 120);
    
    private static final Color HP_BAR_COLOR = new Color(220, 60, 60);
    private static final Color AMMO_BAR_COLOR = new Color(255, 200, 60);
    private static final Color BULLET_COLOR = new Color(255, 215, 0);
    private static final Color ENEMY_BULLET_COLOR = new Color(220, 20, 60);
    
    private static final Color DIALOG_BG = new Color(20, 15, 10, 220);
    private static final Color DIALOG_TEXT = new Color(255, 220, 180);
    private static final Color DIALOG_BORDER = new Color(210, 180, 120);
    
    private static final Color PAUSE_OVERLAY = new Color(0, 0, 0, 180);
    
    private static final Font RETRO_FONT = new Font("Monospaced", Font.BOLD, 14);
    private static final Font DIALOG_FONT = new Font("Serif", Font.BOLD, 16);
    private static final Font PAUSE_FONT = new Font("Serif", Font.BOLD, 48);
    private static final Font PAUSE_INFO_FONT = new Font("SansSerif", Font.PLAIN, 18);
    
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
    
    private void loadAssets() {
        System.out.println("Loading game assets...");
        
        background = AssetManager.loadImage("background.png");
        playerSprite = AssetManager.loadImage("player.png");
        banditSprite = AssetManager.loadImage("bandit.png");
        rockSprite = AssetManager.loadImage("rock.png");
        cowboyAvatar = AssetManager.loadImage("cowboy_avatar.png");
        
        System.out.println("All assets loaded successfully!");
    }
    
    public void setPresenter(GamePresenter presenter) {
        this.presenter = presenter;
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (GamePanel.this.presenter != null && !GamePanel.this.presenter.isGamePaused()) {
                    GamePanel.this.presenter.shoot(e.getX(), e.getY());
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawBackground(g2d);
        drawRocks(g2d);
        drawBullets(g2d);
        drawEnemyBullets(g2d);
        drawPlayer(g2d);
        drawBandits(g2d);
        drawUI(g2d);
        drawDialog(g2d);
        
        if (presenter != null && presenter.isGamePaused()) {
            drawPauseOverlay(g2d);
        }
    }
    
    private void drawBackground(Graphics2D g) {
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }
    }
    
    private void drawPlayer(Graphics2D g) {
        if (playerSprite != null) {
            g.drawImage(playerSprite, player.getX(), player.getY(), 40, 40, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(player.getX(), player.getY(), 40, 40);
        }
    }
    
    private void drawBandits(Graphics2D g) {
        if (bandits != null) {
            for (BanditModel bandit : bandits) {
                if (bandit.isAlive()) {
                    if (banditSprite != null) {
                        g.drawImage(banditSprite, bandit.getX(), bandit.getY(), 40, 40, null);
                    } else {
                        g.setColor(Color.RED);
                        g.fillRect(bandit.getX(), bandit.getY(), 40, 40);
                    }
                }
            }
        }
    }
    
    private void drawRocks(Graphics2D g) {
        if (rocks != null) {
            for (RockModel rock : rocks) {
                if (rockSprite != null) {
                    g.drawImage(rockSprite, 
                        rock.getX(), rock.getY(), 
                        rock.getWidth(), rock.getHeight(), 
                        null);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRect(rock.getX(), rock.getY(), rock.getWidth(), rock.getHeight());
                }
            }
        }
    }
    
    private void drawBullets(Graphics2D g) {
        if (bullets != null) {
            for (BulletModel bullet : bullets) {
                if (bullet.isActive()) {
                    g.setColor(new Color(255, 215, 0, 100));
                    g.fillOval(bullet.getX() - 2, bullet.getY() - 2, 
                              bullet.getSize() + 4, bullet.getSize() + 4);
                    
                    g.setColor(BULLET_COLOR);
                    g.fillOval(bullet.getX(), bullet.getY(), 
                              bullet.getSize(), bullet.getSize());
                }
            }
        }
    }
    
    private void drawEnemyBullets(Graphics2D g) {
        if (enemyBullets != null) {
            for (EnemyBulletModel bullet : enemyBullets) {
                if (bullet.isActive()) {
                    g.setColor(new Color(220, 20, 60, 100));
                    g.fillOval(bullet.getX() - 2, bullet.getY() - 2, 
                              bullet.getSize() + 4, bullet.getSize() + 4);
                    
                    g.setColor(ENEMY_BULLET_COLOR);
                    g.fillOval(bullet.getX(), bullet.getY(), 
                              bullet.getSize(), bullet.getSize());
                }
            }
        }
    }
    
    private void drawUI(Graphics2D g) {
        int uiX = 20;
        int uiY = 20;
        
        // Cowboy Avatar
        int avatarSize = 60;
        int avatarX = 320;
        int avatarY = uiY - 5;
        drawCowboyAvatar(g, avatarX, avatarY, avatarSize);
        
        // Health Bar
        drawRetroFrame(g, uiX, uiY, 280, 28);
        drawRetroFill(g, uiX + 6, uiY + 6, 268, 16, 
                      player.getHp(), player.getMaxHp(), HP_BAR_COLOR);
        
        g.setFont(RETRO_FONT);
        g.setColor(UI_HIGHLIGHT);
        String hpText = "HP: " + player.getHp() + "/" + player.getMaxHp();
        g.drawString(hpText, uiX + 10, uiY + 19);
        
        // Ammo Section
        int ammoY = uiY + 40;
        
        drawRetroLabelBox(g, uiX, ammoY, 80, 24);
        g.setFont(RETRO_FONT);
        g.setColor(UI_HIGHLIGHT);
        g.drawString("Peluru:", uiX + 8, ammoY + 17);
        
        int ammoBarX = uiX + 90;
        drawRetroFrame(g, ammoBarX, ammoY, 190, 24);
        drawRetroFill(g, ammoBarX + 6, ammoY + 6, 178, 12, 
                      player.getAmmo(), player.getMaxAmmo(), AMMO_BAR_COLOR);
        
        g.setColor(UI_HIGHLIGHT);
        String ammoText = player.getAmmo() + "/" + player.getMaxAmmo();
        g.drawString(ammoText, ammoBarX + 10, ammoY + 17);
        
        // Score & Stats
        int scoreY = ammoY + 40;
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        
        // Score
        g.setColor(UI_DARK_BROWN);
        g.drawString("SCORE: " + player.getScore(), uiX + 2, scoreY + 2);
        g.setColor(UI_HIGHLIGHT);
        g.drawString("SCORE: " + player.getScore(), uiX, scoreY);
        
        // Bullets Missed (if presenter available)
        if (presenter != null && presenter.getCurrentStats() != null) {
            int missedY = scoreY + 20;
            g.setColor(UI_DARK_BROWN);
            g.drawString("MISSED: " + presenter.getCurrentStats().getBulletsMissed(), uiX + 2, missedY + 2);
            g.setColor(new Color(255, 100, 100));
            g.drawString("MISSED: " + presenter.getCurrentStats().getBulletsMissed(), uiX, missedY);
        }
        
        // Controls hint (bottom right)
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(255, 255, 255, 150));
        g.drawString("SPACE: Pause/Menu", getWidth() - 150, getHeight() - 10);
    }
    
    private void drawCowboyAvatar(Graphics2D g, int x, int y, int size) {
        Shape originalClip = g.getClip();
        
        Ellipse2D.Double circle = new Ellipse2D.Double(x, y, size, size);
        g.setClip(circle);
        
        if (cowboyAvatar != null) {
            g.drawImage(cowboyAvatar, x, y, size, size, null);
        } else {
            g.setColor(UI_MED_BROWN);
            g.fillOval(x, y, size, size);
            
            g.setColor(UI_LIGHT_BROWN);
            g.fillOval(x + 15, y + 15, 30, 30);
            g.setColor(UI_DARK_BROWN);
            g.fillRect(x + 10, y + 10, 40, 8);
        }
        
        g.setClip(originalClip);
        
        g.setColor(UI_BORDER);
        g.setStroke(new BasicStroke(3));
        g.drawOval(x, y, size, size);
        
        g.setColor(UI_GOLD);
        g.setStroke(new BasicStroke(2));
        g.drawOval(x + 2, y + 2, size - 4, size - 4);
    }
    
    private void drawDialog(Graphics2D g) {
        String dialog = CowboyDialog.getCurrentDialog();
        
        if (dialog.isEmpty()) {
            return;
        }
        
        float opacity = CowboyDialog.getDialogOpacity();
        
        int dialogWidth = 600;
        int dialogHeight = 60;
        int dialogX = (getWidth() - dialogWidth) / 2;
        int dialogY = getHeight() - 100;
        
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        drawDialogBox(g, dialogX, dialogY, dialogWidth, dialogHeight);
        
        g.setFont(DIALOG_FONT);
        g.setColor(DIALOG_TEXT);
        
        FontMetrics fm = g.getFontMetrics();
        int textX = dialogX + (dialogWidth - fm.stringWidth(dialog)) / 2;
        int textY = dialogY + (dialogHeight + fm.getAscent() - fm.getDescent()) / 2;
        
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(dialog, textX + 2, textY + 2);
        
        g.setColor(DIALOG_TEXT);
        g.drawString(dialog, textX, textY);
        
        g.setComposite(originalComposite);
    }
    
    private void drawDialogBox(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(DIALOG_BG);
        g.fillRoundRect(x, y, w, h, 15, 15);
        
        g.setColor(DIALOG_BORDER);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x, y, w, h, 15, 15);
        
        g.setColor(UI_GOLD);
        g.setStroke(new BasicStroke(1));
        g.drawRoundRect(x + 3, y + 3, w - 6, h - 6, 12, 12);
        
        g.setFont(new Font("Serif", Font.BOLD, 20));
        g.setColor(UI_GOLD);
        g.drawString("★", x + 10, y + 25);
        g.drawString("★", x + w - 28, y + 25);
    }
    
    private void drawPauseOverlay(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(PAUSE_OVERLAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Pause text
        g.setFont(PAUSE_FONT);
        g.setColor(Color.WHITE);
        String pauseText = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(pauseText)) / 2;
        int y = getHeight() / 2 - 50;
        
        // Shadow
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(pauseText, x + 3, y + 3);
        
        // Main text
        g.setColor(Color.WHITE);
        g.drawString(pauseText, x, y);
        
        // Instructions
        g.setFont(PAUSE_INFO_FONT);
        String[] instructions = {
            "Press SPACE to Resume",
            "Press ESC to Return to Menu"
        };
        
        int instructY = y + 80;
        for (String instruction : instructions) {
            int instX = (getWidth() - g.getFontMetrics().stringWidth(instruction)) / 2;
            g.setColor(new Color(0, 0, 0, 200));
            g.drawString(instruction, instX + 2, instructY + 2);
            g.setColor(new Color(255, 220, 180));
            g.drawString(instruction, instX, instructY);
            instructY += 35;
        }
    }
    
    private void drawRetroFrame(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(UI_DARK_BROWN);
        g.fillRect(x, y, w, h);
        
        g.setColor(UI_BORDER);
        g.fillRect(x, y, w, 2);
        g.fillRect(x, y, 2, h);
        
        g.setColor(UI_LIGHT_BROWN);
        g.fillRect(x + 2, y + 2, w - 4, 2);
        g.fillRect(x + 2, y + 2, 2, h - 4);
        
        g.setColor(new Color(60, 35, 20));
        g.fillRect(x + 2, y + h - 2, w - 2, 2);
        g.fillRect(x + w - 2, y + 2, 2, h - 2);
        
        g.setColor(UI_MED_BROWN);
        g.fillRect(x + 4, y + h - 4, w - 8, 2);
        g.fillRect(x + w - 4, y + 4, 2, h - 8);
    }
    
    private void drawRetroLabelBox(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(UI_MED_BROWN);
        g.fillRect(x, y, w, h);
        
        g.setColor(UI_BORDER);
        g.fillRect(x, y, w, 2);
        g.fillRect(x, y, 2, h);
        
        g.setColor(UI_DARK_BROWN);
        g.fillRect(x + 2, y + h - 2, w - 2, 2);
        g.fillRect(x + w - 2, y + 2, 2, h - 2);
    }
    
    private void drawRetroFill(Graphics2D g, int x, int y, int w, int h,
                               int current, int max, Color fillColor) {
        float percent = Math.max(0f, Math.min(1f, (float) current / max));
        int fillWidth = (int) (w * percent);
        
        if (fillWidth > 0) {
            g.setColor(fillColor);
            g.fillRect(x, y, fillWidth, h);
            
            g.setColor(new Color(255, 255, 255, 80));
            g.fillRect(x, y, fillWidth, h / 3);
            
            g.setColor(new Color(0, 0, 0, 40));
            g.fillRect(x, y + h - h / 4, fillWidth, h / 4);
            
            g.setColor(new Color(255, 255, 255, 30));
            for (int i = 0; i < fillWidth; i += 4) {
                g.drawLine(x + i, y, x + i, y + h);
            }
        }
    }
}
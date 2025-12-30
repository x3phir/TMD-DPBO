
package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

import model.*;
import presenter.GamePresenter;
import util.AssetManager;
import util.CowboyDialog;

/**
 * GamePanel - Panel utama untuk render game
 * Menggambar semua objek game dan UI
 */
public class GamePanel extends JPanel {
    
    // ==================== REFERENSI OBJEK GAME ====================
    private final PlayerModel player;
    private final List<BulletModel> bullets;
    private final List<EnemyBulletModel> enemyBullets;
    private final List<BanditModel> bandits;
    private final List<RockModel> rocks;
    
    // ==================== SPRITE ASSETS ====================
    private BufferedImage background;
    private BufferedImage playerSprite;
    private BufferedImage banditSprite;
    private BufferedImage rockSprite;
    private BufferedImage cowboyAvatar;
    
    private GamePresenter presenter;
    
    // ==================== WARNA TEMA ====================
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
    
    // ==================== FONT ====================
    private static final Font RETRO_FONT = new Font("Monospaced", Font.BOLD, 14);
    private static final Font DIALOG_FONT = new Font("Serif", Font.BOLD, 16);
    private static final Font PAUSE_FONT = new Font("Serif", Font.BOLD, 48);
    private static final Font PAUSE_INFO_FONT = new Font("SansSerif", Font.PLAIN, 18);
    
    /**
     * Constructor - Inisialisasi panel dan load assets
     */
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
     * Load semua asset gambar
     */
    private void loadAssets() {
        background = AssetManager.loadImage("background.png");
        playerSprite = AssetManager.loadImage("player.png");
        banditSprite = AssetManager.loadImage("bandit.png");
        rockSprite = AssetManager.loadImage("rock.png");
        cowboyAvatar = AssetManager.loadImage("cowboy_avatar.png");
    }
    
    /**
     * Set presenter dan tambah mouse listener untuk menembak
     */
    public void setPresenter(GamePresenter presenter) {
        this.presenter = presenter;
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (presenter != null && !presenter.isGamePaused()) {
                    presenter.shoot(e.getX(), e.getY());
                }
            }
        });
    }
    
    /**
     * Method paint utama - dipanggil setiap repaint()
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing untuk grafis lebih halus
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Render semua objek (urutan penting untuk layering)
        drawBackground(g2d);
        drawRocks(g2d);
        drawBullets(g2d);
        drawEnemyBullets(g2d);
        drawPlayer(g2d);
        drawBandits(g2d);
        drawUI(g2d);
        drawDialog(g2d);
        
        // Overlay pause jika game di-pause
        if (presenter != null && presenter.isGamePaused()) {
            drawPauseOverlay(g2d);
        }
    }
    
    // ==================== RENDER OBJEK GAME ====================
    
    /** Render background */
    private void drawBackground(Graphics2D g) {
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }
    }
    
    /** Render player sprite */
    private void drawPlayer(Graphics2D g) {
        if (playerSprite != null) {
            g.drawImage(playerSprite, player.getX(), player.getY(), 40, 40, null);
        } else {
            // Fallback jika sprite tidak ada
            g.setColor(Color.BLUE);
            g.fillRect(player.getX(), player.getY(), 40, 40);
        }
    }
    
    /** Render semua bandit yang masih hidup */
    private void drawBandits(Graphics2D g) {
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
    
    /** Render semua batu */
    private void drawRocks(Graphics2D g) {
        for (RockModel rock : rocks) {
            if (rockSprite != null) {
                g.drawImage(rockSprite, rock.getX(), rock.getY(), 
                           rock.getWidth(), rock.getHeight(), null);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(rock.getX(), rock.getY(), rock.getWidth(), rock.getHeight());
            }
        }
    }
    
    /** Render peluru player dengan efek glow */
    private void drawBullets(Graphics2D g) {
        for (BulletModel bullet : bullets) {
            if (bullet.isActive()) {
                // Glow effect
                g.setColor(new Color(255, 215, 0, 100));
                g.fillOval(bullet.getX() - 2, bullet.getY() - 2, 
                          bullet.getSize() + 4, bullet.getSize() + 4);
                
                // Peluru utama
                g.setColor(BULLET_COLOR);
                g.fillOval(bullet.getX(), bullet.getY(), 
                          bullet.getSize(), bullet.getSize());
            }
        }
    }
    
    /** Render peluru musuh dengan efek glow */
    private void drawEnemyBullets(Graphics2D g) {
        for (EnemyBulletModel bullet : enemyBullets) {
            if (bullet.isActive()) {
                // Glow effect
                g.setColor(new Color(220, 20, 60, 100));
                g.fillOval(bullet.getX() - 2, bullet.getY() - 2, 
                          bullet.getSize() + 4, bullet.getSize() + 4);
                
                // Peluru utama
                g.setColor(ENEMY_BULLET_COLOR);
                g.fillOval(bullet.getX(), bullet.getY(), 
                          bullet.getSize(), bullet.getSize());
            }
        }
    }
    
    // ==================== RENDER UI ====================
    
    /**
     * Render semua elemen UI (HP bar, ammo bar, score, etc)
     */
    private void drawUI(Graphics2D g) {
        int uiX = 20;
        int uiY = 20;
        
        // HP Bar
        drawRetroFrame(g, uiX, uiY, 280, 28);
        drawRetroFill(g, uiX + 6, uiY + 6, 268, 16, 
                      player.getHp(), player.getMaxHp(), HP_BAR_COLOR);
        
        g.setFont(RETRO_FONT);
        g.setColor(UI_HIGHLIGHT);
        g.drawString("HP: " + player.getHp() + "/" + player.getMaxHp(), uiX + 10, uiY + 19);
        
        // Ammo Section
        int ammoY = uiY + 40;
        drawRetroLabelBox(g, uiX, ammoY, 80, 24);
        g.drawString("Peluru:", uiX + 8, ammoY + 17);
        
        int ammoBarX = uiX + 90;
        drawRetroFrame(g, ammoBarX, ammoY, 190, 24);
        drawRetroFill(g, ammoBarX + 6, ammoY + 6, 178, 12, 
                      player.getAmmo(), player.getMaxAmmo(), AMMO_BAR_COLOR);
        g.drawString(player.getAmmo() + "/" + player.getMaxAmmo(), ammoBarX + 10, ammoY + 17);
        
        // Score & Statistik
        drawGameStats(g, uiX, ammoY + 40);
        
        // Hint kontrol
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(255, 255, 255, 150));
        g.drawString("SPACE: Pause/Menu", getWidth() - 150, getHeight() - 10);
    }
    
    /**
     * Render skor dan statistik game
     */
    private void drawGameStats(Graphics2D g, int x, int y) {
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        
        // Score dengan shadow
        g.setColor(UI_DARK_BROWN);
        g.drawString("SCORE: " + player.getScore(), x + 2, y + 2);
        g.setColor(UI_HIGHLIGHT);
        g.drawString("SCORE: " + player.getScore(), x, y);
        
        // Bullets missed
        if (presenter != null && presenter.getCurrentStats() != null) {
            int missedY = y + 20;
            g.setColor(UI_DARK_BROWN);
            g.drawString("MISSED: " + presenter.getCurrentStats().getBulletsMissed(), x + 2, missedY + 2);
            g.setColor(new Color(255, 100, 100));
            g.drawString("MISSED: " + presenter.getCurrentStats().getBulletsMissed(), x, missedY);
        }
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
        
        // Apply fade effect
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        // Draw box
        drawDialogBox(g, dialogX, dialogY, dialogWidth, dialogHeight);
        
        // Draw text dengan shadow
        g.setFont(DIALOG_FONT);
        FontMetrics fm = g.getFontMetrics();
        int textX = dialogX + (dialogWidth - fm.stringWidth(dialog)) / 2;
        int textY = dialogY + (dialogHeight + fm.getAscent() - fm.getDescent()) / 2;
        
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(dialog, textX + 2, textY + 2);
        
        g.setColor(DIALOG_TEXT);
        g.drawString(dialog, textX, textY);
        
        g.setComposite(originalComposite);
    }
    
    /**
     * Render box untuk dialog dengan border emas
     */
    private void drawDialogBox(Graphics2D g, int x, int y, int w, int h) {
        // Background
        g.setColor(DIALOG_BG);
        g.fillRoundRect(x, y, w, h, 15, 15);
        
        // Border luar
        g.setColor(DIALOG_BORDER);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x, y, w, h, 15, 15);
        
        // Border dalam (emas)
        g.setColor(UI_GOLD);
        g.setStroke(new BasicStroke(1));
        g.drawRoundRect(x + 3, y + 3, w - 6, h - 6, 12, 12);
        
        // Dekorasi bintang
        g.setFont(new Font("Serif", Font.BOLD, 20));
        g.drawString("★", x + 10, y + 25);
        g.drawString("★", x + w - 28, y + 25);
    }
    
    // ==================== PAUSE OVERLAY ====================
    
    /**
     * Render overlay ketika game di-pause
     * Menampilkan teks "PAUSED" dan instruksi
     */
    private void drawPauseOverlay(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(PAUSE_OVERLAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Teks "PAUSED" dengan shadow
        g.setFont(PAUSE_FONT);
        String pauseText = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(pauseText)) / 2;
        int y = getHeight() / 2 - 50;
        
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(pauseText, x + 3, y + 3);
        
        g.setColor(Color.WHITE);
        g.drawString(pauseText, x, y);
        
        // Instruksi
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
    
    // ==================== UI HELPER FUNCTIONS ====================
    
    /**
     * Gambar frame retro 3D untuk UI element
     * Efek emboss dengan highlight dan shadow
     */
    private void drawRetroFrame(Graphics2D g, int x, int y, int w, int h) {
        // Background
        g.setColor(UI_DARK_BROWN);
        g.fillRect(x, y, w, h);
        
        // Highlight (kiri atas)
        g.setColor(UI_BORDER);
        g.fillRect(x, y, w, 2);
        g.fillRect(x, y, 2, h);
        
        g.setColor(UI_LIGHT_BROWN);
        g.fillRect(x + 2, y + 2, w - 4, 2);
        g.fillRect(x + 2, y + 2, 2, h - 4);
        
        // Shadow (kanan bawah)
        g.setColor(new Color(60, 35, 20));
        g.fillRect(x + 2, y + h - 2, w - 2, 2);
        g.fillRect(x + w - 2, y + 2, 2, h - 2);
        
        g.setColor(UI_MED_BROWN);
        g.fillRect(x + 4, y + h - 4, w - 8, 2);
        g.fillRect(x + w - 4, y + 4, 2, h - 8);
    }
    
    /**
     * Gambar label box sederhana untuk UI
     */
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
    
    /**
     * Gambar fill bar dengan efek gradient dan texture
     * Digunakan untuk HP bar dan Ammo bar
     * 
     * @param current Nilai saat ini (HP/Ammo)
     * @param max Nilai maksimal
     * @param fillColor Warna bar
     */
    private void drawRetroFill(Graphics2D g, int x, int y, int w, int h,
                               int current, int max, Color fillColor) {
        // Hitung persentase
        float percent = Math.max(0f, Math.min(1f, (float) current / max));
        int fillWidth = (int) (w * percent);
        
        if (fillWidth > 0) {
            // Fill utama
            g.setColor(fillColor);
            g.fillRect(x, y, fillWidth, h);
            
            // Highlight atas (efek 3D)
            g.setColor(new Color(255, 255, 255, 80));
            g.fillRect(x, y, fillWidth, h / 3);
            
            // Shadow bawah
            g.setColor(new Color(0, 0, 0, 40));
            g.fillRect(x, y + h - h / 4, fillWidth, h / 4);
            
            // Texture garis vertikal
            g.setColor(new Color(255, 255, 255, 30));
            for (int i = 0; i < fillWidth; i += 4) {
                g.drawLine(x + i, y, x + i, y + h);
            }
        }
    }
}
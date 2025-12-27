package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility class to create placeholder asset images
 * Run this once to generate all required sprite files
 */
public class CreateAssets {
    
    public static void main(String[] args) {
        System.out.println("Creating placeholder assets...\n");
        
        // Create assets directory
        File assetsDir = new File("assets/sprites");
        if (!assetsDir.exists()) {
            assetsDir.mkdirs();
            System.out.println("✓ Created directory: " + assetsDir.getAbsolutePath());
        }
        
        // Create all assets
        createBackground();
        createPlayer();
        createBandit();
        createRock();
        
        System.out.println("\n✓ All assets created successfully!");
        System.out.println("Location: " + assetsDir.getAbsolutePath());
    }
    
    private static void createBackground() {
        System.out.println("Creating background.png...");
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desert ground gradient
        GradientPaint groundGradient = new GradientPaint(
            0, 200, new Color(139, 69, 19),
            0, 600, new Color(205, 133, 63)
        );
        g.setPaint(groundGradient);
        g.fillRect(0, 200, 800, 400);
        
        // Sky gradient
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(135, 206, 235),
            0, 200, new Color(255, 255, 224)
        );
        g.setPaint(skyGradient);
        g.fillRect(0, 0, 800, 200);
        
        // Sun
        g.setColor(new Color(255, 255, 0));
        g.fillOval(650, 30, 100, 100);
        g.setColor(new Color(255, 200, 0, 100));
        g.fillOval(630, 10, 140, 140);
        
        // Mountains
        g.setColor(new Color(101, 67, 33));
        int[] xMountain1 = {0, 200, 400};
        int[] yMountain1 = {200, 80, 200};
        g.fillPolygon(xMountain1, yMountain1, 3);
        
        int[] xMountain2 = {300, 500, 700};
        int[] yMountain2 = {200, 100, 200};
        g.fillPolygon(xMountain2, yMountain2, 3);
        
        // Ground texture
        g.setColor(new Color(0, 0, 0, 20));
        for (int i = 0; i < 2000; i++) {
            int x = (int)(Math.random() * 800);
            int y = (int)(Math.random() * 400) + 200;
            g.fillOval(x, y, 2, 2);
        }
        
        // Cacti
        drawCactus(g, 100, 450);
        drawCactus(g, 650, 480);
        
        g.dispose();
        saveImage(img, "assets/sprites/background.png");
    }
    
    private static void drawCactus(Graphics2D g, int x, int y) {
        g.setColor(new Color(34, 139, 34));
        
        // Main trunk
        g.fillRoundRect(x, y, 30, 80, 15, 15);
        
        // Left arm
        g.fillRoundRect(x - 20, y + 20, 20, 40, 10, 10);
        g.fillRoundRect(x - 20, y + 20, 25, 10, 10, 10);
        
        // Right arm
        g.fillRoundRect(x + 30, y + 30, 20, 35, 10, 10);
        g.fillRoundRect(x + 25, y + 30, 25, 10, 10, 10);
        
        // Details
        g.setColor(new Color(50, 205, 50));
        g.fillOval(x + 5, y + 10, 3, 3);
        g.fillOval(x + 20, y + 20, 3, 3);
        g.fillOval(x + 10, y + 40, 3, 3);
    }
    
    private static void createPlayer() {
        System.out.println("Creating player.png...");
        
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Body (blue shirt)
        g.setColor(new Color(30, 144, 255));
        g.fillRect(10, 18, 20, 18);
        
        // Legs (brown pants)
        g.setColor(new Color(139, 69, 19));
        g.fillRect(12, 35, 7, 5);
        g.fillRect(21, 35, 7, 5);
        
        // Head
        g.setColor(new Color(255, 228, 196));
        g.fillOval(13, 8, 14, 14);
        
        // Hat (brown cowboy hat)
        g.setColor(new Color(139, 69, 19));
        g.fillRoundRect(8, 5, 24, 6, 5, 5); // Brim
        g.fillRoundRect(14, 2, 12, 7, 3, 3); // Crown
        
        // Gun
        g.setColor(Color.GRAY);
        g.fillRect(28, 22, 8, 3);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(34, 20, 2, 7);
        
        // Eyes
        g.setColor(Color.BLACK);
        g.fillOval(16, 14, 2, 2);
        g.fillOval(22, 14, 2, 2);
        
        // Smile
        g.drawArc(17, 16, 6, 4, 0, -180);
        
        // Outline
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.drawRect(10, 18, 20, 18);
        g.drawOval(13, 8, 14, 14);
        
        g.dispose();
        saveImage(img, "assets/sprites/player.png");
    }
    
    private static void createBandit() {
        System.out.println("Creating bandit.png...");
        
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Body (red shirt)
        g.setColor(new Color(178, 34, 34));
        g.fillRect(10, 18, 20, 18);
        
        // Legs (black pants)
        g.setColor(Color.BLACK);
        g.fillRect(12, 35, 7, 5);
        g.fillRect(21, 35, 7, 5);
        
        // Head
        g.setColor(new Color(255, 228, 196));
        g.fillOval(13, 8, 14, 14);
        
        // Hat (black outlaw hat)
        g.setColor(Color.BLACK);
        g.fillRoundRect(8, 5, 24, 6, 5, 5); // Brim
        g.fillRoundRect(14, 2, 12, 7, 3, 3); // Crown
        
        // Bandana (red)
        g.setColor(new Color(220, 20, 60));
        g.fillRect(13, 17, 14, 5);
        
        // Gun
        g.setColor(Color.DARK_GRAY);
        g.fillRect(4, 22, 8, 3);
        g.fillRect(2, 20, 2, 7);
        
        // Eyes (angry)
        g.setColor(Color.BLACK);
        g.fillOval(16, 12, 2, 2);
        g.fillOval(22, 12, 2, 2);
        g.drawLine(15, 11, 18, 13);
        g.drawLine(22, 13, 25, 11);
        
        // Outline
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.drawRect(10, 18, 20, 18);
        g.drawOval(13, 8, 14, 14);
        
        g.dispose();
        saveImage(img, "assets/sprites/bandit.png");
    }
    
    private static void createRock() {
        System.out.println("Creating rock.png...");
        
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(8, 56, 48, 6);
        
        // Rock shape (irregular polygon)
        int[] xPoints = {12, 28, 52, 58, 50, 32, 8};
        int[] yPoints = {28, 8, 12, 32, 54, 56, 48};
        
        // Rock base (dark gray)
        g.setColor(new Color(105, 105, 105));
        g.fillPolygon(xPoints, yPoints, 7);
        
        // Rock gradient overlay
        GradientPaint rockGradient = new GradientPaint(
            20, 10, new Color(169, 169, 169, 200),
            50, 50, new Color(105, 105, 105, 0)
        );
        g.setPaint(rockGradient);
        g.fillPolygon(xPoints, yPoints, 7);
        
        // Highlights
        g.setColor(new Color(211, 211, 211, 180));
        g.fillOval(18, 14, 10, 8);
        g.fillOval(38, 18, 8, 6);
        g.fillOval(25, 28, 6, 5);
        
        // Cracks
        g.setColor(new Color(64, 64, 64));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(30, 15, 35, 30);
        g.drawLine(35, 30, 38, 42);
        g.drawLine(20, 35, 30, 40);
        
        // Outline
        g.setColor(new Color(64, 64, 64));
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(xPoints, yPoints, 7);
        
        g.dispose();
        saveImage(img, "assets/sprites/rock.png");
    }
    
    private static void saveImage(BufferedImage img, String filename) {
        try {
            File outputFile = new File(filename);
            ImageIO.write(img, "png", outputFile);
            System.out.println("  ✓ Saved: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("  ✗ Error saving " + filename + ": " + e.getMessage());
        }
    }
}
package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    
    // Possible asset locations
    private static final String[] ASSET_PATHS = {
        "assets/sprites/",
        "assets/",
        "./assets/sprites/",
        "./assets/",
        "../assets/sprites/",
        "../assets/",
        "src/assets/sprites/",
        "src/assets/"
    };
    
    /**
     * Load an image from file with fallback to default
     */
    public static BufferedImage loadImage(String filename) {
        // Check cache first
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }
        
        // Try to load from possible paths
        for (String basePath : ASSET_PATHS) {
            String fullPath = basePath + filename;
            File file = new File(fullPath);
            
            if (file.exists()) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    imageCache.put(filename, img);
                    System.out.println("✓ Loaded: " + fullPath);
                    return img;
                } catch (IOException e) {
                    System.err.println("✗ Error reading: " + fullPath);
                }
            }
        }
        
        // If not found, create default
        System.out.println("⚠ Creating default image for: " + filename);
        BufferedImage defaultImg = createDefaultImage(filename);
        imageCache.put(filename, defaultImg);
        return defaultImg;
    }
    
    /**
     * Create default placeholder images
     */
    private static BufferedImage createDefaultImage(String filename) {
        if (filename.contains("background")) {
            return createDefaultBackground();
        } else if (filename.contains("player")) {
            return createDefaultPlayer();
        } else if (filename.contains("bandit")) {
            return createDefaultBandit();
        } else if (filename.contains("rock")) {
            return createDefaultRock();
        }
        return createGenericPlaceholder(64, 64, Color.MAGENTA);
    }
    
    /**
     * Create default background (800x600)
     */
    private static BufferedImage createDefaultBackground() {
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desert gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(139, 69, 19),      // Brown
            0, 600, new Color(205, 133, 63)    // Tan
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, 800, 600);
        
        // Add some texture
        g.setColor(new Color(0, 0, 0, 20));
        for (int i = 0; i < 1000; i++) {
            int x = (int)(Math.random() * 800);
            int y = (int)(Math.random() * 600);
            g.fillOval(x, y, 2, 2);
        }
        
        // Sky
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(135, 206, 235),    // Sky blue
            0, 200, new Color(255, 255, 255, 50)
        );
        g.setPaint(skyGradient);
        g.fillRect(0, 0, 800, 200);
        
        g.dispose();
        return img;
    }
    
    /**
     * Create default player sprite (40x40)
     */
    private static BufferedImage createDefaultPlayer() {
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Body (blue)
        g.setColor(new Color(30, 144, 255));
        g.fillRect(10, 15, 20, 20);
        
        // Head (beige)
        g.setColor(new Color(255, 228, 196));
        g.fillOval(12, 5, 16, 16);
        
        // Hat (brown)
        g.setColor(new Color(139, 69, 19));
        g.fillRect(8, 3, 24, 5);
        g.fillRect(14, 0, 12, 4);
        
        // Gun (gray)
        g.setColor(Color.GRAY);
        g.fillRect(28, 20, 8, 4);
        
        // Border
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.drawRect(10, 15, 20, 20);
        g.drawOval(12, 5, 16, 16);
        
        g.dispose();
        return img;
    }
    
    /**
     * Create default bandit sprite (40x40)
     */
    private static BufferedImage createDefaultBandit() {
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Body (red)
        g.setColor(new Color(178, 34, 34));
        g.fillRect(10, 15, 20, 20);
        
        // Head (beige)
        g.setColor(new Color(255, 228, 196));
        g.fillOval(12, 5, 16, 16);
        
        // Hat (black)
        g.setColor(Color.BLACK);
        g.fillRect(8, 3, 24, 5);
        g.fillRect(14, 0, 12, 4);
        
        // Gun (dark gray)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(4, 20, 8, 4);
        
        // Border
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.drawRect(10, 15, 20, 20);
        g.drawOval(12, 5, 16, 16);
        
        g.dispose();
        return img;
    }
    
    /**
     * Create default rock sprite (64x64)
     */
    private static BufferedImage createDefaultRock() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Rock shape (irregular polygon)
        int[] xPoints = {10, 30, 54, 60, 50, 30, 5};
        int[] yPoints = {30, 5, 10, 35, 60, 64, 50};
        
        // Shadow
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(5, 55, 55, 8);
        
        // Rock body
        GradientPaint rockGradient = new GradientPaint(
            0, 0, new Color(169, 169, 169),
            64, 64, new Color(105, 105, 105)
        );
        g.setPaint(rockGradient);
        g.fillPolygon(xPoints, yPoints, 7);
        
        // Highlights
        g.setColor(new Color(211, 211, 211, 150));
        g.fillOval(15, 12, 12, 8);
        g.fillOval(38, 18, 8, 6);
        
        // Border
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(xPoints, yPoints, 7);
        
        g.dispose();
        return img;
    }
    
    /**
     * Create generic colored placeholder
     */
    private static BufferedImage createGenericPlaceholder(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        g.drawLine(0, 0, width, height);
        g.drawLine(width, 0, 0, height);
        
        g.dispose();
        return img;
    }
    
    /**
     * Print diagnostic info about asset locations
     */
    public static void printAssetDiagnostics() {
        System.out.println("\n=== ASSET DIAGNOSTICS ===");
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        
        System.out.println("\nSearching for assets in:");
        for (String path : ASSET_PATHS) {
            File dir = new File(path);
            System.out.println("  " + (dir.exists() ? "✓" : "✗") + " " + dir.getAbsolutePath());
        }
        
        System.out.println("\nLooking for files:");
        String[] requiredFiles = {"background.png", "player.png", "bandit.png", "rock.png", "bar.png"};
        for (String filename : requiredFiles) {
            boolean found = false;
            for (String path : ASSET_PATHS) {
                File file = new File(path + filename);
                if (file.exists()) {
                    System.out.println("  ✓ Found: " + file.getAbsolutePath());
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("  ✗ Not found: " + filename + " (will use default)");
            }
        }
        System.out.println("========================\n");
    }
    
    /**
     * Clear the image cache
     */
    public static void clearCache() {
        imageCache.clear();
    }
}
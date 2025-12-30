package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Kelas AssetManager bertanggung jawab untuk memuat, menyimpan (cache),
 * dan menyediakan gambar cadangan (placeholder) jika file aset tidak ditemukan.
 */
public class AssetManager {
    
    // Cache untuk menyimpan gambar yang sudah dimuat agar tidak perlu membaca file dari disk berulang kali.
    // Key: Nama file (String), Value: Data gambar (BufferedImage).
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    
    // Daftar kemungkinan jalur (path) lokasi aset. 
    // Ini membantu program menemukan folder 'assets' baik saat dijalankan dari IDE, Terminal, maupun setelah dikompilasi.
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
     * Memuat gambar dari file dengan mekanisme fallback ke gambar default.
     * * @param filename Nama file gambar yang ingin dimuat (misal: "player.png")
     * @return BufferedImage Objek gambar yang berhasil dimuat atau gambar default.
     */
    public static BufferedImage loadImage(String filename) {
        // Langkah 1: Periksa apakah gambar sudah ada di dalam cache (Memory)
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }
        
        // Langkah 2: Jika tidak ada di cache, coba cari file di setiap jalur yang terdaftar di ASSET_PATHS
        for (String basePath : ASSET_PATHS) {
            String fullPath = basePath + filename;
            File file = new File(fullPath);
            
            if (file.exists()) {
                try {
                    // Jika file ditemukan, baca file tersebut menjadi BufferedImage
                    BufferedImage img = ImageIO.read(file);
                    // Simpan ke cache untuk penggunaan berikutnya
                    imageCache.put(filename, img);
                    System.out.println("✓ Berhasil memuat: " + fullPath);
                    return img;
                } catch (IOException e) {
                    // Terjadi kesalahan saat membaca file (misal: file rusak)
                    System.err.println("✗ Gagal membaca: " + fullPath);
                }
            }
        }
        
        // Langkah 3: Jika file sama sekali tidak ditemukan di semua jalur, buat gambar default (placeholder)
        // Hal ini mencegah game crash (NullPointerException) jika gambar hilang.
        System.out.println("⚠ Menggunakan gambar default untuk: " + filename);
        BufferedImage defaultImg = createDefaultImage(filename);
        imageCache.put(filename, defaultImg);
        return defaultImg;
    }
    
    /**
     * Logika pemilihan gambar default berdasarkan nama file yang dicari.
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
        // Jika tidak cocok dengan kategori manapun, buat kotak magenta (standard industri untuk missing texture)
        return createGenericPlaceholder(64, 64, Color.MAGENTA);
    }
    
    /**
     * Membuat gambar latar belakang default (800x600) menggunakan procedural drawing.
     */
    private static BufferedImage createDefaultBackground() {
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        // Mengaktifkan Antialiasing agar gambar lebih halus
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Menggambar gradasi warna tanah/gurun (Coklat ke Tan)
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(139, 69, 19), 
            0, 600, new Color(205, 133, 63)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, 800, 600);
        
        // Menambahkan tekstur bintik-bintik pasir secara acak
        g.setColor(new Color(0, 0, 0, 20));
        for (int i = 0; i < 1000; i++) {
            int x = (int)(Math.random() * 800);
            int y = (int)(Math.random() * 600);
            g.fillOval(x, y, 2, 2);
        }
        
        // Menggambar gradasi langit biru di bagian atas
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(135, 206, 235),
            0, 200, new Color(255, 255, 255, 50)
        );
        g.setPaint(skyGradient);
        g.fillRect(0, 0, 800, 200);
        
        g.dispose(); // Melepaskan resource grafis
        return img;
    }
    
    /**
     * Membuat sprite pemain sederhana (Biru) secara programatis.
     */
    private static BufferedImage createDefaultPlayer() {
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Tubuh (Kotak Biru)
        g.setColor(new Color(30, 144, 255));
        g.fillRect(10, 15, 20, 20);
        
        // Kepala (Warna Kulit)
        g.setColor(new Color(255, 228, 196));
        g.fillOval(12, 5, 16, 16);
        
        // Topi Cowboy (Coklat)
        g.setColor(new Color(139, 69, 19));
        g.fillRect(8, 3, 24, 5); // Pinggiran topi
        g.fillRect(14, 0, 12, 4); // Bagian atas topi
        
        // Senjata (Abu-abu)
        g.setColor(Color.GRAY);
        g.fillRect(28, 20, 8, 4);
        
        // Garis tepi (Outline) hitam agar sprite lebih jelas
        g.setColor(Color.BLACK);
        g.drawRect(10, 15, 20, 20);
        g.drawOval(12, 5, 16, 16);
        
        g.dispose();
        return img;
    }
    
    /**
     * Membuat sprite musuh/bandit sederhana (Merah).
     */
    private static BufferedImage createDefaultBandit() {
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Tubuh (Merah Tua)
        g.setColor(new Color(178, 34, 34));
        g.fillRect(10, 15, 20, 20);
        
        // Kepala
        g.setColor(new Color(255, 228, 196));
        g.fillOval(12, 5, 16, 16);
        
        // Topi (Hitam)
        g.setColor(Color.BLACK);
        g.fillRect(8, 3, 24, 5);
        g.fillRect(14, 0, 12, 4);
        
        // Senjata menghadap ke kiri
        g.setColor(Color.DARK_GRAY);
        g.fillRect(4, 20, 8, 4);
        
        g.setColor(Color.BLACK);
        g.drawRect(10, 15, 20, 20);
        g.drawOval(12, 5, 16, 16);
        
        g.dispose();
        return img;
    }
    
    /**
     * Membuat gambar batu menggunakan bentuk poligon tidak beraturan.
     */
    private static BufferedImage createDefaultRock() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Titik-titik koordinat untuk membentuk batu yang tidak simetris
        int[] xPoints = {10, 30, 54, 60, 50, 30, 5};
        int[] yPoints = {30, 5, 10, 35, 60, 64, 50};
        
        // Bayangan di bawah batu
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(5, 55, 55, 8);
        
        // Tekstur warna batu dengan gradasi abu-abu
        GradientPaint rockGradient = new GradientPaint(
            0, 0, new Color(169, 169, 169),
            64, 64, new Color(105, 105, 105)
        );
        g.setPaint(rockGradient);
        g.fillPolygon(xPoints, yPoints, 7);
        
        // Menambahkan efek cahaya/pantulan (Highlights)
        g.setColor(new Color(211, 211, 211, 150));
        g.fillOval(15, 12, 12, 8);
        g.fillOval(38, 18, 8, 6);
        
        // Outline batu
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(xPoints, yPoints, 7);
        
        g.dispose();
        return img;
    }
    
    /**
     * Membuat placeholder umum berupa kotak dengan tanda silang.
     * Biasanya muncul jika file tidak ditemukan dan tidak ada kategori defaultnya.
     */
    private static BufferedImage createGenericPlaceholder(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        g.drawLine(0, 0, width, height); // Garis silang \
        g.drawLine(width, 0, 0, height); // Garis silang /
        
        g.dispose();
        return img;
    }
    
    /**
     * Mencetak informasi diagnostik untuk membantu debugging lokasi aset.
     * Sangat berguna jika gambar tidak muncul untuk melihat di mana program mencari file tersebut.
     */
    public static void printAssetDiagnostics() {
        System.out.println("\n=== DIAGNOSTIK ASET ===");
        System.out.println("Direktori Kerja: " + System.getProperty("user.dir"));
        
        System.out.println("\nMencari aset di lokasi berikut:");
        for (String path : ASSET_PATHS) {
            File dir = new File(path);
            System.out.println("  " + (dir.exists() ? "✓" : "✗") + " " + dir.getAbsolutePath());
        }
        
        System.out.println("\nStatus file yang dibutuhkan:");
        String[] requiredFiles = {"background.png", "player.png", "bandit.png", "rock.png"};
        for (String filename : requiredFiles) {
            boolean found = false;
            for (String path : ASSET_PATHS) {
                File file = new File(path + filename);
                if (file.exists()) {
                    System.out.println("  ✓ Ditemukan: " + file.getAbsolutePath());
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("  ✗ Tidak ditemukan: " + filename + " (menggunakan default)");
            }
        }
        System.out.println("========================\n");
    }
    
    /**
     * Membersihkan cache gambar untuk mengosongkan memori RAM.
     */
    public static void clearCache() {
        imageCache.clear();
    }
}
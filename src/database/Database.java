package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database - Kelas untuk mengelola koneksi dan inisialisasi database SQLite
 * Menyimpan history permainan pemain
 */
public class Database {
    
    // URL koneksi database SQLite
    private static final String DATABASE_URL = "jdbc:sqlite:game.db";
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    
    /**
     * Mendapatkan koneksi ke database SQLite
     * @return Connection object atau null jika gagal
     */
    public static Connection getConnection() {
        try {
            // Load driver JDBC SQLite
            Class.forName(JDBC_DRIVER);
            
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            
            if (conn != null) {
                System.out.println("Connected to: " + DATABASE_URL);
            }
            
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: SQLite JDBC driver not found!");
            System.err.println("Download from: https://github.com/xerial/sqlite-jdbc/releases");
            return null;
            
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to database");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Inisialisasi database - Buat tabel jika belum ada
     */
    public static void init() {
        System.out.println("Initializing database...");
        
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.err.println("Failed to initialize: Connection is null");
                return;
            }
            
            createTables(conn);
            System.out.println("Database initialized successfully!");
            
        } catch (Exception e) {
            System.err.println("Error during initialization:");
            e.printStackTrace();
        }
    }
    
    /**
     * Buat tabel-tabel yang diperlukan
     * Tabel history: menyimpan skor dan statistik pemain
     */
    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Tabel history dengan kolom:
            // - id: Primary key auto increment
            // - username: Nama pemain
            // - score: Skor akhir
            // - ammo: Peluru tersisa
            // - bullets_missed: Jumlah tembakan meleset
            // - created_at: Waktu permainan
            String createHistoryTable = """
                CREATE TABLE IF NOT EXISTS history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    score INTEGER NOT NULL DEFAULT 0,
                    ammo INTEGER NOT NULL DEFAULT 0,
                    bullets_missed INTEGER NOT NULL DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            
            stmt.execute(createHistoryTable);
            System.out.println("✓ Table 'history' ready");
            
            // Index untuk query lebih cepat (sort by score)
            String createScoreIndex = """
                CREATE INDEX IF NOT EXISTS idx_score 
                ON history(score DESC)
                """;
            
            stmt.execute(createScoreIndex);
            System.out.println("✓ Index on 'score' ready");
        }
    }
    
    /**
     * Test koneksi database
     * @return true jika berhasil connect, false jika gagal
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
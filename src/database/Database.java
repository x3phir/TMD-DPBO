package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    
    private static final String DATABASE_URL = "jdbc:sqlite:game.db";
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    
    /**
     * Gets a connection to the SQLite database
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        try {
            // Load SQLite JDBC driver
            Class.forName(JDBC_DRIVER);
            
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            
            if (conn != null) {
                System.out.println("Connected to database: " + DATABASE_URL);
            }
            
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: SQLite JDBC driver not found!");
            System.err.println("Please add sqlite-jdbc.jar to your classpath.");
            System.err.println("Download from: https://github.com/xerial/sqlite-jdbc/releases");
            e.printStackTrace();
            return null;
            
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to database");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Initializes the database by creating necessary tables
     */
    public static void init() {
        System.out.println("Initializing database...");
        
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.err.println("Failed to initialize database: Connection is null");
                return;
            }
            
            createTables(conn);
            System.out.println("Database initialized successfully!");
            
        } catch (Exception e) {
            System.err.println("Error during database initialization:");
            e.printStackTrace();
        }
    }
    
    /**
     * Creates all necessary database tables
     */
   
    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Create history table with bullets_missed column
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
            System.out.println("Table 'history' created or already exists");
            
            // Create index for faster queries
            String createScoreIndex = """
                CREATE INDEX IF NOT EXISTS idx_score 
                ON history(score DESC)
                """;
            
            stmt.execute(createScoreIndex);
            System.out.println("Index on 'score' created or already exists");
        }
    }
    
    /**
     * Tests the database connection
     * @return true if connection is successful, false otherwise
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
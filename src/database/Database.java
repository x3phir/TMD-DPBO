package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:game.db";

    public static Connection getConnection() {
        try {
            // coba muat driver jika belum otomatis terdaftar
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Tambahkan sqlite-jdbc.jar ke classpath.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void init() {
        try (Connection c = getConnection()) {
            if (c == null) {
                System.err.println("Gagal membuat koneksi DB. Inisialisasi DB dibatalkan.");
                return;
            }
            try (Statement s = c.createStatement()) {
                s.execute("""
                    CREATE TABLE IF NOT EXISTS history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT,
                        score INTEGER,
                        ammo INTEGER
                    )
                """);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
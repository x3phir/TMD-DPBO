package main;

import database.Database;
import presenter.GamePresenter;
import util.AssetManager;
import util.AudioManager;

public class Main {

    public static void main(String[] args) {
        // Print asset diagnostics
        AssetManager.printAssetDiagnostics();
        // Print audio diagnostics
        AudioManager.printAudioDiagnostics();
        
        // Initialize database
        System.out.println("Initializing database...");
        Database.init();
        
        // Test database connection
        if (Database.testConnection()) {
            System.out.println("✓ Database connection successful!\n");
        } else {
            System.err.println("✗ Database connection failed!\n");
        }
        
        // Start game
        System.out.println("Starting game...\n");
        GamePresenter presenter = new GamePresenter();
        presenter.showMenu();
        
        // Add shutdown hook to cleanup audio
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cleaning up audio resources...");
            AudioManager.cleanup();
        }));
    }
}
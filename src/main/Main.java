package main;

import database.Database;
import presenter.GamePresenter;
import util.AssetManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("  Hide and Seek Cowboy - v1.0");
        System.out.println("=================================\n");
        
        // Print asset diagnostics
        AssetManager.printAssetDiagnostics();
        
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
    }
}
package util;

import java.util.Random;

/**
 * Manages cowboy dialog/subtitle system
 * Random phrases that appear during combat
 */
public class CowboyDialog {
    
    private static final Random random = new Random();
    
    // Cowboy combat phrases (7 phrases as requested)
    private static final String[] COMBAT_PHRASES = {
        "This town ain't big enough for both of us!",
        "Draw, you varmint!",
        "You picked the wrong cowboy, partner!",
        "Say your prayers, outlaw!",
        "Time to meet your maker!",
        "I'm gonna send you to Boot Hill!",
        "Your days of thievin' are over!"
    };
    
    private static String currentDialog = "";
    private static long dialogStartTime = 0;
    private static final long DIALOG_DURATION = 3000; // 3 seconds
    private static long lastDialogTime = 0;
    private static final long MIN_DIALOG_INTERVAL = 5000; // 5 seconds between dialogs
    
    /**
     * Trigger a random dialog
     * Called when player shoots or kills a bandit
     */
    public static void triggerDialog() {
        long currentTime = System.currentTimeMillis();
        
        // Check if enough time has passed since last dialog
        if (currentTime - lastDialogTime < MIN_DIALOG_INTERVAL) {
            return;
        }
        
        // 30% chance to trigger dialog when shooting
        if (random.nextDouble() < 0.3) {
            currentDialog = COMBAT_PHRASES[random.nextInt(COMBAT_PHRASES.length)];
            dialogStartTime = currentTime;
            lastDialogTime = currentTime;
        }
    }
    
    /**
     * Force trigger a dialog (e.g., when killing a bandit)
     */
    public static void triggerDialogForced() {
        long currentTime = System.currentTimeMillis();
        
        // Check if enough time has passed since last dialog
        if (currentTime - lastDialogTime < MIN_DIALOG_INTERVAL) {
            return;
        }
        
        currentDialog = COMBAT_PHRASES[random.nextInt(COMBAT_PHRASES.length)];
        dialogStartTime = currentTime;
        lastDialogTime = currentTime;
    }
    
    /**
     * Get current dialog text
     * Returns empty string if dialog has expired
     */
    public static String getCurrentDialog() {
        long currentTime = System.currentTimeMillis();
        
        // Check if dialog has expired
        if (currentTime - dialogStartTime > DIALOG_DURATION) {
            currentDialog = "";
        }
        
        return currentDialog;
    }
    
    /**
     * Check if dialog is currently active
     */
    public static boolean isDialogActive() {
        return !getCurrentDialog().isEmpty();
    }
    
    /**
     * Clear current dialog
     */
    public static void clearDialog() {
        currentDialog = "";
        dialogStartTime = 0;
    }
    
    /**
     * Get dialog opacity based on time remaining
     * Returns 0.0 to 1.0 for fade out effect
     */
    public static float getDialogOpacity() {
        if (!isDialogActive()) {
            return 0.0f;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - dialogStartTime;
        long remaining = DIALOG_DURATION - elapsed;
        
        // Fade out in last 500ms
        if (remaining < 500) {
            return remaining / 500.0f;
        }
        
        return 1.0f;
    }
}
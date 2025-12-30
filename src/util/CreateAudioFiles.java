package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility to generate placeholder SOUND EFFECTS only
 * Music files (MP3) should be placed manually in assets/audio/
 * Run this once to create all required sound effect files
 */
public class CreateAudioFiles {
    
    private static final float SAMPLE_RATE = 22050.0f;
    
    public static void main(String[] args) {
        System.out.println("Creating placeholder sound effect files...\n");
        System.out.println("NOTE: For music, please place your MP3 files:");
        System.out.println("  - menu_music.mp3");
        System.out.println("  - game_music.mp3");
        System.out.println("in the assets/audio/ folder\n");
        
        // Create audio directory
        File audioDir = new File("assets/audio");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
            System.out.println("✓ Created directory: " + audioDir.getAbsolutePath());
        }
        
        // Create all sound effects
        createShootSound();
        createEnemyShootSound();
        createBanditDeathSound();
        
        System.out.println("\n✓ All sound effects created successfully!");
        System.out.println("Location: " + audioDir.getAbsolutePath());
        System.out.println("\n⚠ REMINDER: Add your own MP3 music files:");
        System.out.println("  - assets/audio/menu_music.mp3");
        System.out.println("  - assets/audio/game_music.mp3");
    }
    
    /**
     * Create player shoot sound effect
     */
    private static void createShootSound() {
        System.out.println("Creating sot.wav...");
        
        byte[] audioData = generateGunshot(0.25f, 1.0f);
        saveWavFile(audioData, "assets/audio/shoot.wav");
    }
    
    /**
     * Create enemy shoot sound effect
     */
    private static void createEnemyShootSound() {
        System.out.println("Creating enemy_shoot.wav...");
        
        byte[] audioData = generateGunshot(0.22f, 0.9f);
        saveWavFile(audioData, "assets/audio/enemy_shoot.wav");
    }
    
    /**
     * Create bandit death sound effect
     */
    private static void createBanditDeathSound() {
        System.out.println("Creating bandit_death.wav...");
        
        byte[] audioData = generateDeathSound(0.6f);
        saveWavFile(audioData, "assets/audio/bandit_death.wav");
    }
    
    /**
     * Generate gunshot sound effect
     */
    private static byte[] generateGunshot(float duration, float intensity) {
        int numSamples = (int) (SAMPLE_RATE * duration);
        byte[] buffer = new byte[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            // Exponential decay envelope
            double envelope = Math.exp(-10.0 * i / numSamples);
            
            // White noise for gunshot
            double noise = (Math.random() * 2 - 1) * envelope;
            
            // Low frequency thump
            double thump = Math.sin(2.0 * Math.PI * i * 100 / SAMPLE_RATE) * envelope * 0.5;
            
            // High frequency crack
            double crack = Math.sin(2.0 * Math.PI * i * 800 / SAMPLE_RATE) * envelope * 0.3;
            
            double sample = (noise * 0.6 + thump + crack) * 0.8 * intensity;
            
            // Convert to 8-bit unsigned
            buffer[i] = (byte) ((sample * 100) + 128);
        }
        
        return buffer;
    }
    
    /**
     * Generate death/hit sound effect
     */
    private static byte[] generateDeathSound(float duration) {
        int numSamples = (int) (SAMPLE_RATE * duration);
        byte[] buffer = new byte[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / numSamples;
            
            // Envelope with quick attack and slower decay
            double envelope = Math.exp(-3.0 * t);
            
            // Descending frequency for "fall" effect
            double freq = 200 * (1 - t * 0.8); // 200 Hz -> 40 Hz
            double angle = 2.0 * Math.PI * i * freq / SAMPLE_RATE;
            
            // Main tone
            double tone = Math.sin(angle) * envelope * 0.5;
            
            // Add some noise
            double noise = (Math.random() * 2 - 1) * envelope * 0.2;
            
            // Low frequency rumble
            double rumble = Math.sin(2.0 * Math.PI * i * 60 / SAMPLE_RATE) * envelope * 0.3;
            
            double sample = tone + noise + rumble;
            
            // Convert to 8-bit unsigned
            buffer[i] = (byte) ((sample * 100) + 128);
        }
        
        return buffer;
    }
    
    /**
     * Save audio data as WAV file with proper header
     */
    private static void saveWavFile(byte[] audioData, String filename) {
        try {
            File outputFile = new File(filename);
            FileOutputStream fos = new FileOutputStream(outputFile);
            
            // WAV file header
            int dataSize = audioData.length;
            int fileSize = dataSize + 36;
            
            // RIFF header
            fos.write("RIFF".getBytes());
            fos.write(intToBytes(fileSize));
            fos.write("WAVE".getBytes());
            
            // Format chunk
            fos.write("fmt ".getBytes());
            fos.write(intToBytes(16));
            fos.write(shortToBytes((short) 1));
            fos.write(shortToBytes((short) 1));
            fos.write(intToBytes((int) SAMPLE_RATE));
            fos.write(intToBytes((int) SAMPLE_RATE));
            fos.write(shortToBytes((short) 1));
            fos.write(shortToBytes((short) 8));
            
            // Data chunk
            fos.write("data".getBytes());
            fos.write(intToBytes(dataSize));
            fos.write(audioData);
            
            fos.close();
            
            System.out.println("  ✓ Saved: " + outputFile.getAbsolutePath() + 
                             " (" + (int)(audioData.length / SAMPLE_RATE * 1000) + " ms)");
            
        } catch (IOException e) {
            System.err.println("  ✗ Error saving " + filename + ": " + e.getMessage());
        }
    }
    
    private static byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }
    
    private static byte[] shortToBytes(short value) {
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF)
        };
    }
}
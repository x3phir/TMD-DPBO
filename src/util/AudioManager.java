package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all game audio including music and sound effects
 * Uses WAV format only (no external dependencies required)
 */
public class AudioManager {
    
    private static final Map<String, Clip> soundCache = new HashMap<>();
    private static Clip currentMusic = null;
    
    private static float musicVolume = 2f;
    private static float sfxVolume = 1f;
    
    // Audio file paths
    private static final String[] AUDIO_PATHS = {
        "assets/audio/",
        "assets/sounds/",
        "./assets/audio/",
        "./assets/sounds/",
        "src/assets/audio/",
        "src/assets/sounds/"
    };
    
    /**
     * Play background music (loops continuously)
     * Supports WAV format only
     */
    public static void playMusic(String filename) {
        stopMusic(); // Stop any current music
        
        try {
            Clip clip = loadSound(filename);
            if (clip != null) {
                currentMusic = clip;
                setVolume(clip, musicVolume);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                System.out.println("♪ Playing music: " + filename);
            }
        } catch (Exception e) {
            System.err.println("Error playing music: " + filename);
            // Don't print full stack trace to avoid spam
        }
    }
    
    /**
     * Stop currently playing music
     */
    public static void stopMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
            currentMusic.close();
            currentMusic = null;
        }
    }
    
    /**
     * Play a sound effect once
     */
    public static void playSoundEffect(String filename) {
        try {
            Clip clip = loadSound(filename);
            if (clip != null) {
                setVolume(clip, sfxVolume);
                clip.setFramePosition(0); // Rewind to beginning
                clip.start();
                
                // Clean up after sound finishes
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            }
        } catch (Exception e) {
            // Silently fail for missing SFX to avoid spam
        }
    }
    
    /**
     * Load a sound file (WAV only)
     */
    private static Clip loadSound(String filename) {
        try {
            // Try to find the audio file
            File audioFile = findAudioFile(filename);
            
            if (audioFile == null) {
                System.err.println("⚠ Audio file not found: " + filename);
                return null;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            
            // Get the format
            AudioFormat baseFormat = audioStream.getFormat();
            
            // Convert to PCM if needed
            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false
            );
            
            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
            
            Clip clip = AudioSystem.getClip();
            clip.open(decodedStream);
            
            return clip;
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format: " + filename);
        } catch (IOException e) {
            System.err.println("IO error loading audio: " + filename);
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + filename);
        }
        
        return null;
    }
    
    /**
     * Find audio file in possible locations
     */
    private static File findAudioFile(String filename) {
        for (String basePath : AUDIO_PATHS) {
            File file = new File(basePath + filename);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
    
    /**
     * Set volume for a clip (0.0 to 1.0)
     */
    private static void setVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float range = max - min;
                float gain = min + (range * volume);
                volumeControl.setValue(gain);
            } catch (IllegalArgumentException e) {
                // Volume control not supported
            }
        }
    }
    
    /**
     * Set music volume (0.0 to 1.0)
     */
    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusic != null) {
            setVolume(currentMusic, musicVolume);
        }
    }
    
    /**
     * Set sound effects volume (0.0 to 1.0)
     */
    public static void setSFXVolume(float volume) {
        sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    /**
     * Check if music is currently playing
     */
    public static boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isRunning();
    }
    
    /**
     * Print audio diagnostics
     */
    public static void printAudioDiagnostics() {
        System.out.println("\n=== AUDIO DIAGNOSTICS ===");
        
        System.out.println("\nSearching for audio files in:");
        for (String path : AUDIO_PATHS) {
            File dir = new File(path);
            System.out.println("  " + (dir.exists() ? "✓" : "✗") + " " + dir.getAbsolutePath());
        }
        
        System.out.println("\nLooking for audio files:");
        String[] requiredFiles = {
            "menu_music.wav",
            "game_music.wav", 
            "shoot.wav",
            "enemy_shoot.wav",
            "bandit_death.wav"
        };
        
        for (String filename : requiredFiles) {
            File file = findAudioFile(filename);
            if (file != null) {
                System.out.println("  ✓ Found: " + filename);
            } else {
                System.out.println("  ✗ Not found: " + filename);
            }
        }
        
        System.out.println("========================\n");
    }
    
    /**
     * Clean up all audio resources
     */
    public static void cleanup() {
        stopMusic();
        for (Clip clip : soundCache.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        soundCache.clear();
    }
}
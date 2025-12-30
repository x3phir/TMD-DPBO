package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mengelola semua audio game termasuk musik latar dan efek suara (SFX).
 * Menggunakan format WAV sebagai standar (tidak memerlukan dependensi eksternal).
 */
public class AudioManager {
    
    // Cache untuk menyimpan suara yang sering digunakan agar tidak membebani memori/CPU.
    private static final Map<String, Clip> soundCache = new HashMap<>();
    
    // Menyimpan referensi musik yang sedang diputar agar bisa dihentikan atau diubah volumenya.
    private static Clip currentMusic = null;
    
    // Variabel kontrol volume (rentang 0.0 hingga 1.0).
    private static float musicVolume = 2f; // Catatan: Sebaiknya default adalah 1.0f (100%)
    private static float sfxVolume = 1f;
    
    // Daftar lokasi folder tempat aplikasi akan mencari file audio.
    private static final String[] AUDIO_PATHS = {
        "assets/audio/",
        "assets/sounds/",
        "./assets/audio/",
        "./assets/sounds/",
        "src/assets/audio/",
        "src/assets/sounds/"
    };
    
    /**
     * Memutar musik latar secara terus-menerus (looping).
     * @param filename Nama file audio (contoh: "background.wav").
     */
    public static void playMusic(String filename) {
        stopMusic(); // Hentikan musik yang sedang berjalan sebelum memutar yang baru.
        
        try {
            Clip clip = loadSound(filename);
            if (clip != null) {
                currentMusic = clip;
                setVolume(clip, musicVolume);
                
                // Mengatur agar musik diputar berulang kali tanpa henti.
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                System.out.println("♪ Sedang memutar musik: " + filename);
            }
        } catch (Exception e) {
            System.err.println("Gagal memutar musik: " + filename);
        }
    }
    
    /**
     * Menghentikan musik latar yang sedang berjalan dan membebaskan sumber daya.
     */
    public static void stopMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
            currentMusic.close(); // Penting untuk mencegah kebocoran memori (memory leak).
            currentMusic = null;
        }
    }
    
    /**
     * Memutar efek suara (SFX) satu kali (contoh: suara tembakan atau ledakan).
     */
    public static void playSoundEffect(String filename) {
        try {
            Clip clip = loadSound(filename);
            if (clip != null) {
                setVolume(clip, sfxVolume);
                clip.setFramePosition(0); // Memastikan suara mulai dari awal (rewind).
                clip.start();
                
                // Menambahkan listener untuk menutup 'clip' secara otomatis setelah selesai diputar.
                // Ini penting karena efek suara sering dipicu berkali-kali.
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            }
        } catch (Exception e) {
            // Gagal memutar SFX diabaikan secara diam-diam agar tidak memenuhi log konsol.
        }
    }
    
    /**
     * Memuat file suara dan mengonversinya ke format PCM standar agar kompatibel dengan sistem.
     */
    private static Clip loadSound(String filename) {
        try {
            File audioFile = findAudioFile(filename);
            
            if (audioFile == null) {
                System.err.println("⚠ File audio tidak ditemukan: " + filename);
                return null;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat baseFormat = audioStream.getFormat();
            
            // Konversi format ke PCM_SIGNED agar suara bisa diproses oleh Java Sound API di berbagai OS.
            // Ini memastikan file WAV dengan encoding berbeda tetap bisa diputar.
            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16, // bit size
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false // bigEndian
            );
            
            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
            
            Clip clip = AudioSystem.getClip();
            clip.open(decodedStream);
            
            return clip;
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Format audio tidak didukung: " + filename);
        } catch (IOException e) {
            System.err.println("Kesalahan IO saat memuat audio: " + filename);
        } catch (LineUnavailableException e) {
            System.err.println("Saluran audio tidak tersedia: " + filename);
        }
        
        return null;
    }
    
    /**
     * Mencari file audio di berbagai direktori yang telah ditentukan.
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
     * Mengatur volume pada Clip audio.
     * @param volume Nilai linear (0.0 ke 1.0).
     */
    private static void setVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                // Mengambil kontrol Gain (Desibel) dari sistem audio.
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                
                // Konversi nilai linear 0.0 - 1.0 ke skala logaritmik Desibel (dB).
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float range = max - min;
                float gain = min + (range * volume);
                
                volumeControl.setValue(gain);
            } catch (IllegalArgumentException e) {
                // Berarti sistem audio atau file tersebut tidak mendukung kontrol volume.
            }
        }
    }
    
    /**
     * Mengatur volume musik latar (0.0 hingga 1.0).
     */
    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusic != null) {
            setVolume(currentMusic, musicVolume);
        }
    }
    
    /**
     * Mengatur volume efek suara (0.0 hingga 1.0).
     */
    public static void setSFXVolume(float volume) {
        sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    /**
     * Mengecek apakah ada musik yang sedang aktif diputar.
     */
    public static boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isRunning();
    }
    
    /**
     * Mencetak status diagnostik untuk mempermudah pengecekan file audio yang hilang.
     */
    public static void printAudioDiagnostics() {
        System.out.println("\n=== DIAGNOSTIK AUDIO ===");
        
        System.out.println("\nMencari file audio di:");
        for (String path : AUDIO_PATHS) {
            File dir = new File(path);
            System.out.println("  " + (dir.exists() ? "✓" : "✗") + " " + dir.getAbsolutePath());
        }
        
        System.out.println("\nStatus file audio utama:");
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
                System.out.println("  ✓ Ditemukan: " + filename);
            } else {
                System.out.println("  ✗ Tidak ada: " + filename);
            }
        }
        
        System.out.println("========================\n");
    }
    
    /**
     * Membersihkan semua sumber daya audio. Harus dipanggil saat game ditutup.
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
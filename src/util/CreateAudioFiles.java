package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utilitas untuk menghasilkan file EFEK SUARA (SFX) placeholder saja.
 * File musik (WAV/MP3) harus diletakkan secara manual di assets/audio/.
 * Jalankan kelas ini sekali untuk membuat semua file efek suara yang diperlukan.
 */
public class CreateAudioFiles {
    
    // Frekuensi sampel (Hz). 22050Hz cukup untuk suara lo-fi/game retro.
    private static final float SAMPLE_RATE = 22050.0f;
    
    public static void main(String[] args) {
        System.out.println("Membuat file efek suara placeholder...\n");
        System.out.println("CATATAN: Untuk musik, harap letakkan file Anda secara manual:");
        System.out.println("  - menu_music.wav");
        System.out.println("  - game_music.wav");
        System.out.println("di dalam folder assets/audio/\n");
        
        // Membuat direktori audio jika belum ada
        File audioDir = new File("assets/audio");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
            System.out.println("✓ Berhasil membuat direktori: " + audioDir.getAbsolutePath());
        }
        
        // Membuat semua efek suara melalui sintesis matematis
        createShootSound();
        createEnemyShootSound();
        createBanditDeathSound();
        
        System.out.println("\n✓ Semua efek suara berhasil dibuat!");
        System.out.println("Lokasi: " + audioDir.getAbsolutePath());
    }
    
    /**
     * Membuat efek suara tembakan pemain.
     */
    private static void createShootSound() {
        System.out.println("Membuat shoot.wav...");
        
        // Menghasilkan suara tembakan dengan durasi 0.25 detik
        byte[] audioData = generateGunshot(0.25f, 1.0f);
        saveWavFile(audioData, "assets/audio/shoot.wav");
    }
    
    /**
     * Membuat efek suara tembakan musuh.
     */
    private static void createEnemyShootSound() {
        System.out.println("Membuat enemy_shoot.wav...");
        
        // Menghasilkan suara tembakan yang sedikit lebih pendek dan pelan
        byte[] audioData = generateGunshot(0.22f, 0.9f);
        saveWavFile(audioData, "assets/audio/enemy_shoot.wav");
    }
    
    /**
     * Membuat efek suara kematian bandit.
     */
    private static void createBanditDeathSound() {
        System.out.println("Membuat bandit_death.wav...");
        
        // Menghasilkan suara jatuh/kematian berdurasi 0.6 detik
        byte[] audioData = generateDeathSound(0.6f);
        saveWavFile(audioData, "assets/audio/bandit_death.wav");
    }
    
    /**
     * Menghasilkan data audio tembakan menggunakan kombinasi Noise dan Sinus.
     */
    private static byte[] generateGunshot(float duration, float intensity) {
        int numSamples = (int) (SAMPLE_RATE * duration);
        byte[] buffer = new byte[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            // Envelope: Suara keras di awal dan memudar secara eksponensial
            double envelope = Math.exp(-10.0 * i / numSamples);
            
            // White Noise: Memberikan kesan "ledakan" atau percikan api
            double noise = (Math.random() * 2 - 1) * envelope;
            
            // Low Frequency Thump: Memberikan kesan "dentuman" (bass)
            double thump = Math.sin(2.0 * Math.PI * i * 100 / SAMPLE_RATE) * envelope * 0.5;
            
            // High Frequency Crack: Memberikan kesan "pecahan" peluru (treble)
            double crack = Math.sin(2.0 * Math.PI * i * 800 / SAMPLE_RATE) * envelope * 0.3;
            
            // Menggabungkan semua komponen suara
            double sample = (noise * 0.6 + thump + crack) * 0.8 * intensity;
            
            // Konversi ke format 8-bit unsigned (0-255, dengan 128 sebagai titik tengah/hening)
            buffer[i] = (byte) ((sample * 100) + 128);
        }
        
        return buffer;
    }
    
    /**
     * Menghasilkan suara kematian dengan nada yang menurun (Pitch Down).
     */
    private static byte[] generateDeathSound(float duration) {
        int numSamples = (int) (SAMPLE_RATE * duration);
        byte[] buffer = new byte[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / numSamples;
            
            // Envelope: Memudar perlahan
            double envelope = Math.exp(-3.0 * t);
            
            // Frekuensi Menurun: Memberikan efek objek yang "jatuh" (200Hz ke 40Hz)
            double freq = 200 * (1 - t * 0.8); 
            double angle = 2.0 * Math.PI * i * freq / SAMPLE_RATE;
            
            // Nada utama
            double tone = Math.sin(angle) * envelope * 0.5;
            
            // Menambahkan sedikit noise agar suara terdengar lebih kasar/natural
            double noise = (Math.random() * 2 - 1) * envelope * 0.2;
            
            // Rumble: Suara getaran rendah
            double rumble = Math.sin(2.0 * Math.PI * i * 60 / SAMPLE_RATE) * envelope * 0.3;
            
            double sample = tone + noise + rumble;
            buffer[i] = (byte) ((sample * 100) + 128);
        }
        
        return buffer;
    }
    
    /**
     * Menyimpan data audio mentah menjadi file .WAV yang valid.
     * Mengikuti spesifikasi struktur Header RIFF/WAVE.
     */
    private static void saveWavFile(byte[] audioData, String filename) {
        try {
            File outputFile = new File(filename);
            FileOutputStream fos = new FileOutputStream(outputFile);
            
            // Ukuran data dan total ukuran file
            int dataSize = audioData.length;
            int fileSize = dataSize + 36;
            
            // --- HEADER RIFF ---
            fos.write("RIFF".getBytes());
            fos.write(intToBytes(fileSize)); // Ukuran file total
            fos.write("WAVE".getBytes());
            
            // --- FORMAT CHUNK (fmt ) ---
            fos.write("fmt ".getBytes());
            fos.write(intToBytes(16)); // Panjang format chunk (16 byte untuk PCM)
            fos.write(shortToBytes((short) 1)); // Format audio (1 = Uncompressed PCM)
            fos.write(shortToBytes((short) 1)); // Jumlah channel (1 = Mono)
            fos.write(intToBytes((int) SAMPLE_RATE)); // Sample Rate
            fos.write(intToBytes((int) SAMPLE_RATE)); // Byte Rate (SampleRate * Channels * BitsPerSample/8)
            fos.write(shortToBytes((short) 1)); // Block Align
            fos.write(shortToBytes((short) 8)); // Bits Per Sample (8 bit)
            
            // --- DATA CHUNK ---
            fos.write("data".getBytes());
            fos.write(intToBytes(dataSize)); // Ukuran data audio
            fos.write(audioData); // Menulis data sampel suara
            
            fos.close();
            
            System.out.println("  ✓ Tersimpan: " + outputFile.getAbsolutePath() + 
                               " (" + (int)(audioData.length / SAMPLE_RATE * 1000) + " ms)");
            
        } catch (IOException e) {
            System.err.println("  ✗ Gagal menyimpan " + filename + ": " + e.getMessage());
        }
    }
    
    // Konversi integer ke byte array (Little Endian)
    private static byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }
    
    // Konversi short ke byte array (Little Endian)
    private static byte[] shortToBytes(short value) {
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF)
        };
    }
}
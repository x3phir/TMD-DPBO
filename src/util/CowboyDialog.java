package util;

import java.util.Random;

/**
 * Mengelola sistem dialog/subtitle ala koboi.
 * Berisi frasa acak yang muncul selama pertempuran untuk menambah atmosfer permainan.
 */
public class CowboyDialog {
    
    private static final Random random = new Random();
    
    // Daftar frasa pertempuran koboi (7 frasa sesuai permintaan)
    private static final String[] COMBAT_PHRASES = {
        "This town ain't big enough for both of us!", // Kota ini tidak cukup besar untuk kita berdua!
        "Draw, you varmint!",                        // Cabut senjatamu, hama!
        "You picked the wrong cowboy, partner!",     // Kau memilih koboi yang salah, kawan!
        "Say your prayers, outlaw!",                 // Berdoalah, penjahat!
        "Time to meet your maker!",                  // Waktunya menghadap sang pencipta!
        "I'm gonna send you to Boot Hill!",          // Aku akan mengirimmu ke pemakaman!
        "Your days of thievin' are over!"            // Hari-harimu mencuri sudah berakhir!
    };
    
    private static String currentDialog = "";        // Teks dialog yang sedang aktif
    private static long dialogStartTime = 0;         // Waktu kapan dialog mulai muncul
    private static final long DIALOG_DURATION = 3000; // Durasi dialog tampil (3 detik)
    
    private static long lastDialogTime = 0;          // Waktu terakhir dialog dipicu
    private static final long MIN_DIALOG_INTERVAL = 5000; // Jeda minimal antar dialog (5 detik) agar tidak mengganggu
    
    /**
     * Memicu dialog secara acak.
     * Biasanya dipanggil saat pemain menembak.
     */
    public static void triggerDialog() {
        long currentTime = System.currentTimeMillis();
        
        // Cek apakah jeda waktu minimal sejak dialog terakhir sudah terpenuhi
        if (currentTime - lastDialogTime < MIN_DIALOG_INTERVAL) {
            return;
        }
        
        // Memiliki peluang 30% untuk memicu dialog saat dipanggil
        // Ini mencegah teks muncul terlalu sering setiap kali pemain menekan tombol tembak.
        if (random.nextDouble() < 0.3) {
            currentDialog = COMBAT_PHRASES[random.nextInt(COMBAT_PHRASES.length)];
            dialogStartTime = currentTime;
            lastDialogTime = currentTime;
        }
    }
    
    /**
     * Memaksa dialog untuk muncul (Forced).
     * Digunakan untuk kejadian penting, misalnya saat berhasil membunuh bandit.
     */
    public static void triggerDialogForced() {
        long currentTime = System.currentTimeMillis();
        
        // Tetap menghormati jeda minimal agar tidak terjadi tumpang tindih teks
        if (currentTime - lastDialogTime < MIN_DIALOG_INTERVAL) {
            return;
        }
        
        // Memilih frasa acak dan langsung menampilkannya
        currentDialog = COMBAT_PHRASES[random.nextInt(COMBAT_PHRASES.length)];
        dialogStartTime = currentTime;
        lastDialogTime = currentTime;
    }
    
    /**
     * Mendapatkan teks dialog saat ini.
     * Mengembalikan string kosong jika durasi dialog sudah habis.
     */
    public static String getCurrentDialog() {
        long currentTime = System.currentTimeMillis();
        
        // Cek apakah dialog sudah melewati batas durasi tampil
        if (currentTime - dialogStartTime > DIALOG_DURATION) {
            currentDialog = "";
        }
        
        return currentDialog;
    }
    
    /**
     * Mengecek apakah ada dialog yang sedang aktif di layar.
     */
    public static boolean isDialogActive() {
        return !getCurrentDialog().isEmpty();
    }
    
    /**
     * Menghapus dialog yang sedang tampil secara paksa.
     */
    public static void clearDialog() {
        currentDialog = "";
        dialogStartTime = 0;
    }
    
    /**
     * Menghitung nilai transparansi (opacity) dialog berdasarkan sisa waktu.
     * Digunakan untuk efek memudar (fade out) agar transisi visual lebih halus.
     * @return Nilai float antara 0.0 (transparan) hingga 1.0 (jelas).
     */
    public static float getDialogOpacity() {
        if (!isDialogActive()) {
            return 0.0f;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - dialogStartTime;
        long remaining = DIALOG_DURATION - elapsed;
        
        // Mulai memudar (fade out) dalam 500 milidetik terakhir durasi tampil
        if (remaining < 500) {
            // Menghasilkan nilai yang mengecil dari 1.0 ke 0.0
            return remaining / 500.0f;
        }
        
        return 1.0f; // Tetap solid jika waktu masih banyak
    }
}
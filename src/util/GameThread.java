package util;

import presenter.GamePresenter;

/**
 * GameThread bertanggung jawab untuk menjalankan "Game Loop".
 * Loop ini memastikan logika permainan diperbarui secara berkala di latar belakang
 * tanpa menghentikan (freezing) antarmuka pengguna (UI).
 */
public class GameThread extends Thread {

    // Flag untuk mengontrol apakah thread harus terus berjalan atau berhenti
    private boolean running = true;

    // Referensi ke presenter untuk memicu pembaruan logika game
    private GamePresenter presenter;

    /**
     * Konstruktor GameThread.
     * @param presenter Objek yang berisi logika pembaruan game.
     */
    public GameThread(GamePresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Metode utama yang dijalankan saat thread dimulai (start).
     * Berisi logika Game Loop.
     */
    @Override
    public void run() {
        // Selama variabel running bernilai true, game akan terus diperbarui
        while (running) {
            // 1. Perbarui logika game (posisi karakter, cek tabrakan, dll)
            presenter.updateGame();

            try {
                /*
                 * 2. Mengatur kecepatan frame (Frame Rate).
                 * Thread tidur selama 16 milidetik untuk mencapai target ~60 Frame Per Second (FPS).
                 * Rumus: 1000ms / 60 FPS = 16.66ms per frame.
                 */
                Thread.sleep(16); 
            } catch (InterruptedException e) {
                // Menangani gangguan jika thread dihentikan paksa oleh sistem
                e.printStackTrace();
            }
        }
    }

    /**
     * Berhenti menjalankan loop game secara aman.
     */
    public void stopGame() {
        running = false;
    }
}
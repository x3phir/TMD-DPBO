package util;

import presenter.GamePresenter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Menangani input papan ketik (keyboard) untuk pergerakan pemain.
 * Kelas ini mendengarkan penekanan tombol panah (arrow keys) sesuai spesifikasi.
 */
public class InputHandler extends KeyAdapter {

    private GamePresenter presenter;
    
    // Kecepatan gerak karakter setiap kali tombol ditekan (dalam piksel)
    private static final int MOVE_SPEED = 10;

    /**
     * Konstruktor InputHandler.
     * @param presenter Referensi ke GamePresenter untuk mengirim perintah gerak.
     */
    public InputHandler(GamePresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Dipanggil secara otomatis oleh Java ketika sebuah tombol ditekan.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // Mengidentifikasi tombol mana yang ditekan menggunakan KeyCode
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                // Bergerak ke atas: kurangi nilai Y (sumbu Y negatif di komputer adalah ke atas)
                presenter.movePlayer(0, -MOVE_SPEED);
                break;
            case KeyEvent.VK_DOWN:
                // Bergerak ke bawah: tambahkan nilai Y
                presenter.movePlayer(0, MOVE_SPEED);
                break;
            case KeyEvent.VK_LEFT:
                // Bergerak ke kiri: kurangi nilai X
                presenter.movePlayer(-MOVE_SPEED, 0);
                break;
            case KeyEvent.VK_RIGHT:
                // Bergerak ke kanan: tambahkan nilai X
                presenter.movePlayer(MOVE_SPEED, 0);
                break;
        }
    }
}
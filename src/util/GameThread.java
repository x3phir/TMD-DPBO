package util;

import presenter.GamePresenter;

public class GameThread extends Thread {

    private boolean running = true;

    private GamePresenter presenter;

    public GameThread(GamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void run() {
        while (running) {
            presenter.updateGame();

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopGame() {
        running = false;
    }
}

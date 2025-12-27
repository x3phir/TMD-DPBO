package util;

import presenter.GamePresenter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {

    private GamePresenter presenter;

    public InputHandler(GamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                presenter.movePlayer(0, -10);
                break;
            case KeyEvent.VK_DOWN:
                presenter.movePlayer(0, 10);
                break;
            case KeyEvent.VK_LEFT:
                presenter.movePlayer(-10, 0);
                break;
            case KeyEvent.VK_RIGHT:
                presenter.movePlayer(10, 0);
                break;
        }
    }
}

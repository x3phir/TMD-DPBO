package util;

import presenter.GamePresenter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Handles keyboard input for player movement
 * Uses arrow keys as specified
 */
public class InputHandler extends KeyAdapter {

    private GamePresenter presenter;
    private static final int MOVE_SPEED = 10;

    public InputHandler(GamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Arrow key movement
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                presenter.movePlayer(0, -MOVE_SPEED);
                break;
            case KeyEvent.VK_DOWN:
                presenter.movePlayer(0, MOVE_SPEED);
                break;
            case KeyEvent.VK_LEFT:
                presenter.movePlayer(-MOVE_SPEED, 0);
                break;
            case KeyEvent.VK_RIGHT:
                presenter.movePlayer(MOVE_SPEED, 0);
                break;
        }
    }
}
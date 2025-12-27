package view;

import presenter.GamePresenter;
import util.InputHandler;


import javax.swing.*;

public class GameView extends JFrame {

    private GamePanel gamePanel;

    public GameView(GamePresenter presenter) {
    setTitle("Game Play");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    addKeyListener(new InputHandler(presenter));
    setFocusable(true);
    requestFocusInWindow();
    }


    public void setGamePanel(GamePanel panel) {
        this.gamePanel = panel;
        add(gamePanel);
        revalidate();
        repaint();
    }

    public void refresh() {
    if (gamePanel != null) {
        gamePanel.repaint();
    }
    }
}
package view;

import presenter.GamePresenter;

import javax.swing.*;
import java.awt.*;

public class MenuView extends JFrame {

    private GamePresenter presenter;

    public MenuView(GamePresenter presenter) {
        this.presenter = presenter;

        setTitle("Hide and Seek Cowboy");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> presenter.startGame());

        setLayout(new GridBagLayout());
        add(playButton);
    }
}

package view;

import presenter.GamePresenter;
import model.PlayerStatsModel;
import util.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * GameView
 * - Window utama gameplay
 * - Menangani input keyboard
 * - Menampilkan GamePanel & Game Over Dialog
 */
public class GameView extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private GamePanel gamePanel;
    private final GamePresenter presenter;

    public GameView(GamePresenter presenter) {
        this.presenter = presenter;
        initWindow();
        initInput();
    }

    /* ================= WINDOW SETUP ================= */

    private void initWindow() {
        setTitle("Hide and Seek Challenge");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /* ================= INPUT ================= */

    private void initInput() {
        addKeyListener(new InputHandler(presenter));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> presenter.togglePause();
                    case KeyEvent.VK_ESCAPE -> {
                        if (presenter.isGamePaused()) {
                            presenter.returnToMenu();
                        }
                    }
                }
            }
        });
    }

    /* ================= GAME PANEL ================= */

    public void setGamePanel(GamePanel panel) {
        if (gamePanel != null) remove(gamePanel);
        gamePanel = panel;
        add(gamePanel);
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    public void refresh() {
        if (gamePanel != null) gamePanel.repaint();
    }

    /* ================= GAME OVER ================= */

    public void showGameOverScreen(PlayerStatsModel stats) {
        JDialog dialog = new JDialog(this, "GAME OVER", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel mainPanel = createGameOverPanel(stats, dialog);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createGameOverPanel(PlayerStatsModel stats, JDialog dialog) {
        JPanel panel = new GradientPanel();
        panel.setLayout(null);

        panel.add(createTitle());
        panel.add(createSkull());
        panel.add(createStatsPanel(stats));
        panel.add(createBackButton(dialog));

        return panel;
    }

    /* ================= UI COMPONENTS ================= */

    private JLabel createTitle() {
        JLabel label = new JLabel("GAME OVER", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 64));
        label.setBounds(50, 30, 500, 80);
        label.setForeground(new Color(255, 80, 80));
        return label;
    }

    private JLabel createSkull() {
        JLabel skull = new JLabel("☠", SwingConstants.CENTER);
        skull.setFont(new Font("Serif", Font.BOLD, 48));
        skull.setBounds(250, 110, 100, 60);
        skull.setForeground(new Color(255, 200, 200));
        return skull;
    }

    private JPanel createStatsPanel(PlayerStatsModel stats) {
        JPanel panel = new RoundedPanel();
        panel.setLayout(new GridLayout(5, 1, 8, 8));
        panel.setBounds(100, 190, 400, 200);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setOpaque(false);

        Font font = new Font("Monospaced", Font.BOLD, 18);

        panel.add(stat("Player: " + stats.getUsername(), font));
        panel.add(stat("Score: " + stats.getScore(), font));
        panel.add(stat("Bullets Fired: " + stats.getBulletsFired(), font));
        panel.add(stat("Bullets Missed: " + stats.getBulletsMissed(), font));
        panel.add(stat("Ammo Left: " + stats.getBulletsRemaining(), font));

        return panel;
    }

    private JLabel stat(String text, Font font) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createBackButton(JDialog dialog) {
        JButton button = new StyledButton("Back to Menu");
        button.setBounds(200, 410, 200, 50);
        button.addActionListener(e -> dialog.dispose());
        return button;
    }

    /* ================= CUSTOM PANELS ================= */

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                0, 0, new Color(40, 20, 20),
                0, getHeight(), new Color(90, 40, 40)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(200, 60, 60));
            g2.setStroke(new BasicStroke(4));
            g2.drawRect(5, 5, getWidth() - 10, getHeight() - 10);
        }
    }

    private static class RoundedPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2.setColor(new Color(200, 150, 100));
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
        }
    }

    private static class StyledButton extends JButton {
        public StyledButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 18));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(getModel().isRollover()
                ? new Color(220, 80, 80)
                : new Color(180, 60, 60));

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
        }
    }
}

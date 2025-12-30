package view;

import presenter.GamePresenter;
import model.PlayerStatsModel;
import util.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameView extends JFrame {

    private GamePanel gamePanel;
    private GamePresenter presenter;

    public GameView(GamePresenter presenter) {
        this.presenter = presenter;
        
        setTitle("Game Play - Hide and Seek Challenge");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addKeyListener(new InputHandler(presenter));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    presenter.togglePause();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (presenter.isGamePaused()) {
                        presenter.returnToMenu();
                    }
                }
            }
        });
        
        setFocusable(true);
        requestFocusInWindow();
    }

    public void setGamePanel(GamePanel panel) {
        if (gamePanel != null) {
            remove(gamePanel);
        }
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
    
    public void showGameOverScreen(PlayerStatsModel stats) {
        JDialog gameOverDialog = new JDialog(this, "GAME OVER", true);
        gameOverDialog.setSize(600, 500);
        gameOverDialog.setLocationRelativeTo(this);
        gameOverDialog.setUndecorated(true);
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(40, 20, 20),
                    0, getHeight(), new Color(80, 40, 40)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Border
                g2d.setColor(new Color(200, 50, 50));
                g2d.setStroke(new BasicStroke(5));
                g2d.drawRect(5, 5, getWidth() - 10, getHeight() - 10);
            }
        };
        
        mainPanel.setLayout(null);
        
        // "GAME OVER" Title
        JLabel gameOverLabel = new JLabel("GAME OVER", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String text = getText();
                Font font = getFont();
                FontMetrics fm = g2d.getFontMetrics(font);
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                // Shadow effect
                for (int i = 5; i > 0; i--) {
                    g2d.setColor(new Color(0, 0, 0, 50 * i));
                    g2d.setFont(font);
                    g2d.drawString(text, x + i, y + i);
                }
                
                // Main text with gradient
                GradientPaint textGradient = new GradientPaint(
                    0, 0, new Color(255, 100, 100),
                    0, getHeight(), new Color(200, 50, 50)
                );
                g2d.setPaint(textGradient);
                g2d.drawString(text, x, y);
                
                // Outline
                g2d.setColor(new Color(100, 0, 0));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawString(text, x - 1, y - 1);
            }
        };
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 64));
        gameOverLabel.setBounds(50, 30, 500, 80);
        mainPanel.add(gameOverLabel);
        
        // Skull decoration (optional)
        JLabel skullLabel = new JLabel("☠", SwingConstants.CENTER);
        skullLabel.setFont(new Font("Serif", Font.BOLD, 48));
        skullLabel.setForeground(new Color(255, 200, 200));
        skullLabel.setBounds(250, 110, 100, 60);
        mainPanel.add(skullLabel);
        
        // Stats Panel
        JPanel statsPanel = createStatsPanel(stats);
        statsPanel.setBounds(100, 190, 400, 200);
        mainPanel.add(statsPanel);
        
        // OK Button
        JButton okButton = new JButton("Back to Menu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(150, 50, 50));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(220, 80, 80));
                } else {
                    g2d.setColor(new Color(200, 60, 60));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.setColor(new Color(255, 200, 200));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
                
                g2d.setColor(new Color(255, 255, 255));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(getText(), x + 2, y + 2);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, y);
            }
        };
        okButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        okButton.setBounds(200, 410, 200, 50);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setContentAreaFilled(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> gameOverDialog.dispose());
        mainPanel.add(okButton);
        
        gameOverDialog.add(mainPanel);
        gameOverDialog.setVisible(true);
    }
    
    private JPanel createStatsPanel(PlayerStatsModel stats) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent background
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border
                g2d.setColor(new Color(200, 150, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
            }
        };
        
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        Font statFont = new Font("Monospaced", Font.BOLD, 18);
        Color textColor = new Color(255, 220, 180);
        
        // Player name
        JLabel nameLabel = createStatLabel("Player: " + stats.getUsername(), statFont, textColor);
        panel.add(nameLabel);
        
        // Score
        JLabel scoreLabel = createStatLabel("Final Score: " + stats.getScore(), statFont, new Color(255, 200, 100));
        panel.add(scoreLabel);
        
        // Bullets fired
        JLabel firedLabel = createStatLabel("Bullets Fired: " + stats.getBulletsFired(), statFont, textColor);
        panel.add(firedLabel);
        
        // Bullets missed
        JLabel missedLabel = createStatLabel("Bullets Missed: " + stats.getBulletsMissed(), statFont, new Color(255, 100, 100));
        panel.add(missedLabel);
        
        // Bullets remaining
        JLabel remainingLabel = createStatLabel("Bullets Remaining: " + stats.getBulletsRemaining(), statFont, textColor);
        panel.add(remainingLabel);
        
        return panel;
    }
    
    private JLabel createStatLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.drawString(getText(), x + 2, y + 2);
                
                // Main text
                g2d.setColor(getForeground());
                g2d.drawString(getText(), x, y);
            }
        };
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
}
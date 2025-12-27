package view;

import presenter.GamePresenter;
import model.HistoryModel;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MenuView extends JFrame {

    private GamePresenter presenter;
    private BufferedImage backgroundImage;
    private JTextField usernameField;
    private Font rdrFont;
    private Font rdrFontLarge;

    // RDR2 Color Palette
    private static final Color RDR_RED = new Color(200, 40, 40);
    private static final Color RDR_CREAM = new Color(235, 220, 195);
    private static final Color RDR_DARK = new Color(20, 15, 10);
    private static final Color RDR_BROWN = new Color(90, 60, 40);
    private static final Color RDR_GOLD = new Color(210, 180, 120);
    private static final Color RDR_WHITE = new Color(255, 255, 255);

    public MenuView(GamePresenter presenter) {
        this.presenter = presenter;
        
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        loadFonts();
        loadBackground();
        initComponents();
    }

    private void loadFonts() {
        try {
            // Coba load font custom dari file
            String[] fontPaths = {
                "assets/fonts/rdr.ttf",
                "assets/fonts/western.ttf",
                "assets/rdr.ttf"
            };

            Font baseFont = null;
            for (String path : fontPaths) {
                File fontFile = new File(path);
                if (fontFile.exists()) {
                    baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                    System.out.println("Custom font loaded from: " + path);
                    break;
                }
            }

            if (baseFont != null) {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(baseFont);
                rdrFont = baseFont.deriveFont(Font.PLAIN, 18f);
                rdrFontLarge = baseFont.deriveFont(Font.BOLD, 72f);
            } else {
                // Fallback ke font sistem yang mirip Western
                System.out.println("Custom font not found, using fallback fonts");
                rdrFont = new Font("Serif", Font.BOLD, 18);
                rdrFontLarge = new Font("Serif", Font.BOLD, 72);
            }

        } catch (Exception e) {
            System.err.println("Error loading fonts: " + e.getMessage());
            // Ultimate fallback
            rdrFont = new Font("Serif", Font.BOLD, 18);
            rdrFontLarge = new Font("Serif", Font.BOLD, 72);
        }
    }

    private void loadBackground() {
        try {
            String[] possiblePaths = {
                "assets/sprites/menu_background.png",
                "assets/menu_background.png",
                "assets/sprites/background.png",
                "assets/background.png",
                "menu_background.png"
            };

            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                    System.out.println("Background loaded from: " + path);
                    return;
                }
            }

            System.out.println("Background image not found, creating default background");
            createDefaultBackground();

        } catch (IOException e) {
            System.err.println("Error loading background: " + e.getMessage());
            createDefaultBackground();
        }
    }

    private void createDefaultBackground() {
        backgroundImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = backgroundImage.createGraphics();
        
        // RDR2 style gradient - dark dramatic
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(40, 30, 25),
            0, 600, new Color(15, 10, 8)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, 800, 600);
        
        g.dispose();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background with slight darkening
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                    
                    // Vignette effect (darker at edges)
                    GradientPaint vignette = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        0, getHeight(), new Color(0, 0, 0, 120)
                    );
                    g2d.setPaint(vignette);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        mainPanel.setLayout(null); // Absolute positioning for RDR2 style

        // Menu items at bottom right (RDR2 style)
        JPanel menuPanel = createRDRMenuPanel();
        menuPanel.setBounds(400, 600, 850, 60); 
        mainPanel.add(menuPanel);

        // Username field at bottom left
        JPanel usernamePanel = createUsernamePanel();
        usernamePanel.setBounds(20, 520, 300, 60);
        mainPanel.add(usernamePanel);

        setContentPane(mainPanel);
    }

    private JLabel createRDRTitle() {
        JLabel title = new JLabel("HIDE & SEEK", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String text = getText();
                Font font = getFont();
                FontMetrics fm = g2d.getFontMetrics(font);
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                // Multiple shadow layers for depth
                g2d.setFont(font);
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(text, x + 4, y + 4);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(text, x + 2, y + 2);
                
                // Main text with RDR red
                g2d.setColor(RDR_RED);
                g2d.drawString(text, x, y);
                
                // Outline effect
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(new Color(100, 20, 20));
                g2d.drawString(text, x - 1, y - 1);
            }
        };
        
        title.setFont(rdrFontLarge);
        title.setOpaque(false);
        return title;
    }

    private JPanel createRDRMenuPanel() {
    JPanel panel = new JPanel();
    // Menggunakan FlowLayout agar otomatis berjejer ke kanan
    panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 25, 0)); 
    panel.setOpaque(false);

    String[] menuItems = {"START GAME", "LEADERBOARD", "HOW TO PLAY", "EXIT"};
    Runnable[] actions = {
        () -> startGame(),
        () -> showLeaderboard(),
        () -> showHowToPlay(),
        () -> System.exit(0)
    };

    for (int i = 0; i < menuItems.length; i++) {
        JLabel menuItem = createRDRMenuItem(menuItems[i], actions[i]);
        panel.add(menuItem);
    }

    return panel;
    }

    private JLabel createRDRMenuItem(String text, Runnable action) {
        JLabel label = new JLabel(text) {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String itemText = getText();
                Font font = getFont();
                FontMetrics fm = g2d.getFontMetrics(font);
                int x = getWidth() - fm.stringWidth(itemText);
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                g2d.setFont(font);
                
                if (hovered) {
                    // Glowing effect when hovered
                    g2d.setColor(new Color(255, 255, 255, 150));
                    g2d.drawString(itemText, x - 1, y);
                    g2d.drawString(itemText, x + 1, y);
                    g2d.setColor(RDR_WHITE);
                } else {
                    g2d.setColor(RDR_GOLD);
                }
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.drawString(itemText, x + 2, y + 2);
                
                // Main text
                g2d.setColor(hovered ? RDR_CREAM : RDR_GOLD);
                g2d.drawString(itemText, x, y);
            }
        };

        label.setFont(rdrFont);
        label.setOpaque(false);
        label.setPreferredSize(new Dimension(180, 50));
        label.setMaximumSize(new Dimension(260, 35));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Hover effect
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                try {
                    java.lang.reflect.Field field = label.getClass().getDeclaredField("hovered");
                    field.setAccessible(true);
                    field.set(label, true);
                } catch (Exception ex) {
                    // Fallback: just repaint
                }
                label.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                try {
                    java.lang.reflect.Field field = label.getClass().getDeclaredField("hovered");
                    field.setAccessible(true);
                    field.set(label, false);
                } catch (Exception ex) {
                    // Fallback: just repaint
                }
                label.repaint();
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return label;
    }

    private JPanel createUsernamePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel("NAME:");
        label.setFont(rdrFont.deriveFont(16f));
        label.setForeground(RDR_GOLD);

        usernameField = new JTextField(12);
        usernameField.setFont(new Font("Monospaced", Font.BOLD, 16));
        usernameField.setText("Player1");
        usernameField.setBackground(new Color(30, 25, 20, 200));
        usernameField.setForeground(RDR_CREAM);
        usernameField.setCaretColor(RDR_CREAM);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RDR_BROWN, 2),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        panel.add(label);
        panel.add(usernameField);

        return panel;
    }

    private void startGame() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your name!", 
                "Name Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        presenter.startGame(username);
    }

    private void showLeaderboard() {
        List<HistoryModel> history = presenter.loadHistory();

        JDialog dialog = new JDialog(this, "Leaderboard", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(RDR_DARK);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(RDR_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        titleLabel.setFont(rdrFont.deriveFont(32f));
        titleLabel.setForeground(RDR_RED);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"RANK", "PLAYER", "SCORE", "AMMO"};
        Object[][] data = new Object[history.size()][4];

        for (int i = 0; i < history.size(); i++) {
            HistoryModel h = history.get(i);
            data[i][0] = "#" + (i + 1);
            data[i][1] = h.getUsername();
            data[i][2] = h.getScore();
            data[i][3] = h.getAmmo();
        }

        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("Monospaced", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setBackground(new Color(30, 25, 20));
        table.setForeground(RDR_CREAM);
        table.setGridColor(RDR_BROWN);
        table.setSelectionBackground(RDR_BROWN);
        table.setSelectionForeground(RDR_CREAM);
        table.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 14));
        table.getTableHeader().setBackground(RDR_BROWN);
        table.getTableHeader().setForeground(RDR_CREAM);
        table.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(RDR_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(RDR_BROWN, 2));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeButton = createStyledButton("CLOSE", RDR_BROWN);
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(RDR_DARK);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showHowToPlay() {
        String message = """
            ═══════════════════════════════════════
                        HOW TO PLAY
            ═══════════════════════════════════════
            
            CONTROLS:
            • Arrow Keys (↑↓←→) - Move Character
            • Mouse Click - Shoot Towards Cursor
            
            GAMEPLAY:
            • Survive Waves of Bandits
            • Kill Bandits to Earn Points (+100)
            • Avoid Enemy Bullets (Red)
            • Collect Ammo When Enemies Miss (+1)
            • Don't Let Your HP Reach Zero!
            
            STRATEGY:
            • Use Rocks for Cover
            • Keep Moving to Dodge Bullets
            • Manage Your Ammo Wisely
            • Eliminate Bandits Before They Surround You
            
            ═══════════════════════════════════════
                    Good Luck, Cowboy!
            ═══════════════════════════════════════
            """;

        JTextArea textArea = new JTextArea(message);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(RDR_DARK);
        textArea.setForeground(RDR_CREAM);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JOptionPane.showMessageDialog(this, 
            textArea, 
            "How to Play", 
            JOptionPane.PLAIN_MESSAGE);
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(baseColor.brighter());
                } else {
                    g2d.setColor(baseColor);
                }

                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Border
                g2d.setColor(RDR_GOLD);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);

                // Text
                g2d.setColor(RDR_CREAM);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
            }
        };

        button.setFont(rdrFont);
        button.setPreferredSize(new Dimension(200, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}
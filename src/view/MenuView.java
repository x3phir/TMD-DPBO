package view;

import presenter.GamePresenter;
import model.PlayerStatsModel;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

public class MenuView extends JFrame {

    private GamePresenter presenter;
    private BufferedImage backgroundImage;
    private BufferedImage logoImage;
    private JTextField usernameField;
    private Font rdrFont;
    private Font rdrFontLarge;
    private JTable statsTable;
    
    // RDR2 Redemption Color Palette
    private static final Color RDR_RED = new Color(180, 30, 30);
    private static final Color RDR_BRIGHT_RED = new Color(220, 50, 50);
    private static final Color RDR_CREAM = new Color(240, 230, 210);
    private static final Color RDR_DARK = new Color(15, 10, 8);
    private static final Color RDR_BLACK = new Color(20, 15, 10);

    public MenuView(GamePresenter presenter) {
        this.presenter = presenter;
        
        setTitle("HIDE AND SEEK REDEMPTION");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        loadFonts();
        loadBackground();
        loadLogo();
        initComponents();
    }

    private void loadFonts() {
        try {
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
                rdrFontLarge = baseFont.deriveFont(Font.BOLD, 56f);
            } else {
                System.out.println("Custom font not found, using fallback fonts");
                rdrFont = new Font("Serif", Font.BOLD, 18);
                rdrFontLarge = new Font("Serif", Font.BOLD, 56);
            }

        } catch (Exception e) {
            System.err.println("Error loading fonts: " + e.getMessage());
            rdrFont = new Font("Serif", Font.BOLD, 18);
            rdrFontLarge = new Font("Serif", Font.BOLD, 56);
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

        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
            createDefaultBackground();
        }
    }

    private void loadLogo() {
        try {
            String[] possiblePaths = {
                "assets/sprites/logo.png",
                "assets/logo.png",
                "assets/sprites/hide_and_seek_logo.png",
                "assets/hide_and_seek_logo.png",
                "logo.png"
            };

            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists()) {
                    logoImage = ImageIO.read(file);
                    System.out.println("Logo loaded from: " + path);
                    return;
                }
            }

            System.out.println("Logo image not found");

        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
    }

    private void createDefaultBackground() {
        backgroundImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = backgroundImage.createGraphics();
        
        // Dark red/brown gradient like RDR
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(60, 20, 15),
            0, 720, new Color(30, 10, 8)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, 1280, 720);
        
        g.dispose();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                }
                
                // Dark vignette overlay
                RadialGradientPaint radial = new RadialGradientPaint(
                    getWidth() / 2f, getHeight() / 2f, getWidth() * 0.8f,
                    new float[]{0f, 0.7f, 1f},
                    new Color[]{
                        new Color(0, 0, 0, 0),
                        new Color(0, 0, 0, 100),
                        new Color(0, 0, 0, 180)
                    }
                );
                g2d.setPaint(radial);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        mainPanel.setLayout(null);

        // Logo (top left)
        if (logoImage != null) {
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
            logoLabel.setBounds(120, 30, 400, 150);
            mainPanel.add(logoLabel);
        } else {
            // Title as fallback
            JLabel titleLabel = createRedemptionTitle();
            titleLabel.setBounds(120, 30, 400, 150);
            mainPanel.add(titleLabel);
        }

        // Action buttons - Redemption style (top right)
        JButton playButton = createRedemptionButton("START", RDR_BRIGHT_RED);
        playButton.setBounds(480, 120, 320, 80);
        playButton.addActionListener(e -> startGame());
        mainPanel.add(playButton);

        JButton quitButton = createRedemptionButton("QUIT", RDR_DARK);
        quitButton.setBounds(820, 120, 260, 80);
        quitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(quitButton);

        // Stats Table with dark theme (center)
        JPanel tablePanel = createRedemptionStatsTable();
        tablePanel.setBounds(140, 230, 1000, 250);
        mainPanel.add(tablePanel);

        // Username section (bottom center)
        JPanel usernamePanel = createRedemptionUsernamePanel();
        usernamePanel.setBounds(300, 500, 680, 60);
        mainPanel.add(usernamePanel);

        setContentPane(mainPanel);
    }

    private JLabel createRedemptionTitle() {
        JLabel title = new JLabel("HIDE AND SEEK", SwingConstants.LEFT) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String text = "HIDE AND SEEK";
                String subtitle = "REDEMPTION II";
                Font mainFont = getFont();
                Font subFont = mainFont.deriveFont(Font.BOLD, 24f);
                
                // Main title
                g2d.setFont(mainFont);
                int x = 10;
                int y = 60;
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.drawString(text, x + 3, y + 3);
                
                // Main text
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);
                
                // Subtitle
                g2d.setFont(subFont);
                g2d.setColor(RDR_BRIGHT_RED);
                g2d.drawString(subtitle, x, y + 35);
            }
        };
        
        title.setFont(rdrFontLarge.deriveFont(Font.BOLD, 48f));
        title.setOpaque(false);
        return title;
    }

    private JPanel createRedemptionUsernamePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 12));
        panel.setOpaque(false);

        JLabel nameLabel = new JLabel("USERNAME");
        nameLabel.setFont(rdrFont.deriveFont(Font.BOLD, 22f));
        nameLabel.setForeground(RDR_CREAM);
        panel.add(nameLabel);

        usernameField = new JTextField("Player1", 20);
        usernameField.setFont(new Font("SansSerif", Font.BOLD, 20));
        usernameField.setBackground(RDR_RED);
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RDR_BLACK, 3),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.add(usernameField);

        return panel;
    }

    private JPanel createRedemptionStatsTable() {
        JPanel containerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dark background with slight transparency
                g2d.setColor(new Color(0, 0, 0, 220));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Red border
                g2d.setColor(RDR_RED);
                g2d.setStroke(new BasicStroke(6));
                g2d.drawRect(0, 0, getWidth(), getHeight());
            }
        };
        
        containerPanel.setLayout(new BorderLayout(0, 0));
        containerPanel.setOpaque(false);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load data from database dynamically
        List<model.HistoryModel> historyList = presenter.loadHistory();
        
        String[] columnNames = {"USERNAME", "SCORE", "MISSED SHOT", "AMMO LEFT"};
        Object[][] data;
        
        if (historyList.isEmpty()) {
            data = new Object[][] {
                {"---", "---", "---", "---"},
                {"---", "---", "---", "---"}
            };
        } else {
            int rows = Math.min(2, historyList.size());
            data = new Object[2][4];
            
            for (int i = 0; i < rows; i++) {
                model.HistoryModel h = historyList.get(i);
                data[i][0] = h.getUsername().toUpperCase();
                data[i][1] = String.valueOf(h.getScore());
                data[i][2] = String.valueOf(h.getBulletsMissed());
                data[i][3] = String.valueOf(h.getAmmo());
            }
            
            for (int i = rows; i < 2; i++) {
                data[i][0] = "---";
                data[i][1] = "---";
                data[i][2] = "---";
                data[i][3] = "---";
            }
        }
        
        statsTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Table styling - Redemption black and red
        statsTable.setFont(new Font("SansSerif", Font.BOLD, 20));
        statsTable.setRowHeight(70);
        statsTable.setGridColor(RDR_BLACK);
        statsTable.setShowGrid(true);
        statsTable.setBackground(RDR_RED);
        statsTable.setForeground(Color.WHITE);
        statsTable.setSelectionBackground(RDR_BRIGHT_RED);
        statsTable.setSelectionForeground(Color.WHITE);
        statsTable.setIntercellSpacing(new Dimension(2, 2));
        
        // Header styling - Black with white text
        statsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 18));
        statsTable.getTableHeader().setBackground(RDR_BLACK);
        statsTable.getTableHeader().setForeground(RDR_CREAM);
        statsTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, RDR_RED));
        statsTable.getTableHeader().setReorderingAllowed(false);
        statsTable.getTableHeader().setPreferredSize(new Dimension(0, 50));
        
        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(RDR_RED);
        centerRenderer.setForeground(Color.WHITE);
        for (int i = 0; i < statsTable.getColumnCount(); i++) {
            statsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    private JButton createRedemptionButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = baseColor;
                if (getModel().isPressed()) {
                    color = baseColor.darker();
                } else if (getModel().isRollover()) {
                    color = baseColor.brighter();
                }

                // Simple rectangular button
                g2d.setColor(color);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Black border
                g2d.setColor(RDR_BLACK);
                g2d.setStroke(new BasicStroke(5));
                g2d.drawRect(0, 0, getWidth(), getHeight());

                // Text
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                // Text shadow
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.drawString(getText(), textX + 2, textY + 2);
                
                // Main text
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), textX, textY);
            }
        };

        button.setFont(rdrFont.deriveFont(Font.BOLD, 36f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void startGame() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your username!", 
                "Name Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        saveCurrentStats();
        presenter.startGame(username);
    }

    private void saveCurrentStats() {
        for (int i = 0; i < statsTable.getRowCount(); i++) {
            String username = (String) statsTable.getValueAt(i, 0);
            int score = parseIntSafe((String) statsTable.getValueAt(i, 1));
            int missed = parseIntSafe((String) statsTable.getValueAt(i, 2));
            int remaining = parseIntSafe((String) statsTable.getValueAt(i, 3));
            
            PlayerStatsModel stats = new PlayerStatsModel(username);
            stats.setScore(score);
            stats.setBulletsRemaining(remaining);
            
            presenter.addPlayerStats(stats);
        }
    }

    public void updateStatsTable() {
        List<model.HistoryModel> historyList = presenter.loadHistory();
        
        String[] columnNames = {"USERNAME", "SCORE", "MISSED SHOT", "AMMO LEFT"};
        Object[][] data;
        
        if (historyList.isEmpty()) {
            data = new Object[][] {
                {"---", "---", "---", "---"},
                {"---", "---", "---", "---"}
            };
        } else {
            int rows = Math.min(2, historyList.size());
            data = new Object[2][4];
            
            for (int i = 0; i < rows; i++) {
                model.HistoryModel h = historyList.get(i);
                data[i][0] = h.getUsername().toUpperCase();
                data[i][1] = String.valueOf(h.getScore());
                data[i][2] = String.valueOf(h.getBulletsMissed());
                data[i][3] = String.valueOf(h.getAmmo());
            }
            
            for (int i = rows; i < 2; i++) {
                data[i][0] = "---";
                data[i][1] = "---";
                data[i][2] = "---";
                data[i][3] = "---";
            }
        }
        
        statsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        
        // Reapply styling
        statsTable.setFont(new Font("SansSerif", Font.BOLD, 20));
        statsTable.setRowHeight(70);
        statsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 18));
        statsTable.getTableHeader().setBackground(RDR_BLACK);
        statsTable.getTableHeader().setForeground(RDR_CREAM);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(RDR_RED);
        centerRenderer.setForeground(Color.WHITE);
        for (int i = 0; i < statsTable.getColumnCount(); i++) {
            statsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            // Refresh table data when menu becomes visible
            updateStatsTable();
        }
        super.setVisible(visible);
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
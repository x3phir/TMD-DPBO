package view;

import presenter.GamePresenter;
import model.PlayerStatsModel;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

public class MenuView extends JFrame {

    private GamePresenter presenter;
    private BufferedImage backgroundImage;
    private JTextField usernameField;
    private Font rdrFont;
    private Font rdrFontLarge;
    private JTable statsTable;
    
    // RDR2/Western Color Palette
    private static final Color RDR_RED = new Color(200, 40, 40);
    private static final Color RDR_CREAM = new Color(235, 220, 195);
    private static final Color RDR_DARK = new Color(20, 15, 10);
    private static final Color RDR_BROWN = new Color(90, 60, 40);
    private static final Color RDR_GOLD = new Color(210, 180, 120);
    private static final Color RDR_PARCHMENT = new Color(230, 215, 185);

    public MenuView(GamePresenter presenter) {
        this.presenter = presenter;
        
        setTitle("HIDE AND SEEK THE CHALLENGE");
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

    private void createDefaultBackground() {
        backgroundImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = backgroundImage.createGraphics();
        
        // Western desert sunset gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 140, 60),      // Orange sunset
            0, 720, new Color(139, 69, 19)      // Desert brown
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
                
                // Vignette effect
                GradientPaint vignette = new GradientPaint(
                    0, 0, new Color(0, 0, 0, 0),
                    0, getHeight(), new Color(0, 0, 0, 100)
                );
                g2d.setPaint(vignette);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        mainPanel.setLayout(null);

        // Title with Western style
        JLabel titleLabel = createWesternTitle();
        titleLabel.setBounds(200, 30, 880, 100);
        mainPanel.add(titleLabel);

        // Username section with Western styling
        JPanel usernamePanel = createWesternUsernamePanel();
        usernamePanel.setBounds(400, 150, 480, 60);
        mainPanel.add(usernamePanel);

        // Stats Table with Western wooden board style
        JPanel tablePanel = createWesternStatsTable();
        tablePanel.setBounds(140, 240, 1000, 300);
        mainPanel.add(tablePanel);

        // Action buttons
        JButton playButton = createWesternButton("PLAY GAME", new Color(180, 100, 50));
        playButton.setBounds(350, 580, 250, 70);
        playButton.addActionListener(e -> startGame());
        mainPanel.add(playButton);

        JButton quitButton = createWesternButton("QUIT", new Color(140, 60, 40));
        quitButton.setBounds(680, 580, 250, 70);
        quitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(quitButton);

        // Western decoration elements
        addWesternDecorations(mainPanel);

        setContentPane(mainPanel);
    }

    private JLabel createWesternTitle() {
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
                for (int i = 6; i > 0; i--) {
                    g2d.setColor(new Color(0, 0, 0, 40 * i));
                    g2d.drawString(text, x + i, y + i);
                }
                
                // Main text with Western red
                g2d.setColor(new Color(220, 50, 50));
                g2d.drawString(text, x, y);
                
                // Outline effect
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(new Color(139, 69, 19));
                g2d.drawString(text, x - 1, y - 1);
            }
        };
        
        title.setFont(rdrFontLarge);
        title.setOpaque(false);
        return title;
    }

    private JPanel createWesternUsernamePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Wooden board background
                GradientPaint wood = new GradientPaint(
                    0, 0, new Color(139, 90, 43),
                    0, getHeight(), new Color(101, 67, 33)
                );
                g2d.setPaint(wood);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2d.setColor(new Color(70, 50, 30));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
            }
        };
        
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setOpaque(false);

        JLabel nameLabel = new JLabel("COWBOY NAME:");
        nameLabel.setFont(rdrFont.deriveFont(Font.BOLD, 20f));
        nameLabel.setForeground(RDR_CREAM);
        panel.add(nameLabel);

        usernameField = new JTextField("Player1", 15);
        usernameField.setFont(new Font("Monospaced", Font.BOLD, 18));
        usernameField.setBackground(new Color(245, 235, 210));
        usernameField.setForeground(RDR_DARK);
        usernameField.setCaretColor(RDR_DARK);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 50, 30), 3),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        panel.add(usernameField);

        return panel;
    }

    private JPanel createWesternStatsTable() {
        JPanel containerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Wooden board background
                GradientPaint wood = new GradientPaint(
                    0, 0, new Color(160, 110, 60),
                    0, getHeight(), new Color(120, 80, 40)
                );
                g2d.setPaint(wood);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Wood grain effect
                g2d.setColor(new Color(0, 0, 0, 20));
                for (int i = 0; i < getHeight(); i += 10) {
                    g2d.fillRect(0, i, getWidth(), 2);
                }
                
                // Border nails effect
                g2d.setColor(new Color(80, 60, 40));
                int[] nailX = {20, getWidth() - 20, 20, getWidth() - 20};
                int[] nailY = {20, 20, getHeight() - 20, getHeight() - 20};
                for (int i = 0; i < 4; i++) {
                    g2d.fillOval(nailX[i] - 8, nailY[i] - 8, 16, 16);
                    g2d.setColor(new Color(60, 40, 20));
                    g2d.fillOval(nailX[i] - 5, nailY[i] - 5, 10, 10);
                    g2d.setColor(new Color(80, 60, 40));
                }
                
                // Border
                g2d.setColor(new Color(70, 50, 30));
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);
            }
        };
        
        containerPanel.setLayout(new BorderLayout(10, 10));
        containerPanel.setOpaque(false);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Table title
        JLabel tableTitle = new JLabel("⭐ GUNSLINGER RECORDS ⭐", SwingConstants.CENTER);
        tableTitle.setFont(rdrFont.deriveFont(Font.BOLD, 24f));
        tableTitle.setForeground(RDR_CREAM);
        containerPanel.add(tableTitle, BorderLayout.NORTH);

        // Load data from database dynamically
        List<model.HistoryModel> historyList = presenter.loadHistory();
        
        String[] columnNames = {"Username", "Skor", "Peluru Meleset", "Sisa Peluru"};
        Object[][] data;
        
        if (historyList.isEmpty()) {
            // Show empty state
            data = new Object[][] {
                {"---", "---", "---", "---"},
                {"---", "---", "---", "---"},
                {"---", "---", "---", "---"}
            };
        } else {
            // Fill with database data (top 3)
            int rows = Math.min(3, historyList.size());
            data = new Object[3][4]; // Always 3 rows
            
            for (int i = 0; i < rows; i++) {
                model.HistoryModel h = historyList.get(i);
                data[i][0] = h.getUsername();
                data[i][1] = String.valueOf(h.getScore());
                data[i][2] = String.valueOf(h.getBulletsMissed());
                data[i][3] = String.valueOf(h.getAmmo());
            }
            
            // Fill remaining rows with empty
            for (int i = rows; i < 3; i++) {
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
        
        // Table styling
        statsTable.setFont(new Font("Monospaced", Font.PLAIN, 16));
        statsTable.setRowHeight(50);
        statsTable.setGridColor(new Color(70, 50, 30));
        statsTable.setShowGrid(true);
        statsTable.setBackground(new Color(230, 215, 185));
        statsTable.setForeground(RDR_DARK);
        statsTable.setSelectionBackground(new Color(180, 140, 100));
        statsTable.setSelectionForeground(RDR_DARK);
        
        // Header styling
        statsTable.getTableHeader().setFont(new Font("Serif", Font.BOLD, 18));
        statsTable.getTableHeader().setBackground(new Color(139, 90, 43));
        statsTable.getTableHeader().setForeground(RDR_CREAM);
        statsTable.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(70, 50, 30), 2));
        statsTable.getTableHeader().setReorderingAllowed(false);
        statsTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < statsTable.getColumnCount(); i++) {
            statsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 50, 30), 2));
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    private JButton createWesternButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = baseColor;
                if (getModel().isPressed()) {
                    color = baseColor.darker().darker();
                } else if (getModel().isRollover()) {
                    color = baseColor.brighter();
                }

                // Button background with wood texture
                GradientPaint gradient = new GradientPaint(
                    0, 0, color,
                    0, getHeight(), color.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Border
                g2d.setColor(color.darker().darker());
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);

                // Inner highlight
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 15, 15);

                // Text
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                // Text shadow
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.drawString(getText(), textX + 3, textY + 3);
                
                // Main text
                g2d.setColor(RDR_CREAM);
                g2d.drawString(getText(), textX, textY);
            }
        };

        button.setFont(rdrFont.deriveFont(Font.BOLD, 24f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void addWesternDecorations(JPanel panel) {
        // Rope decoration (top)
        JLabel ropeTop = new JLabel("～～～～～～～～～～～～～～～～～～～～", SwingConstants.CENTER);
        ropeTop.setFont(new Font("SansSerif", Font.BOLD, 24));
        ropeTop.setForeground(new Color(139, 90, 43));
        ropeTop.setBounds(0, 10, 1280, 30);
        panel.add(ropeTop);

        // Star decorations
        JLabel star1 = new JLabel("★");
        star1.setFont(new Font("Serif", Font.BOLD, 48));
        star1.setForeground(RDR_GOLD);
        star1.setBounds(50, 50, 60, 60);
        panel.add(star1);

        JLabel star2 = new JLabel("★");
        star2.setFont(new Font("Serif", Font.BOLD, 48));
        star2.setForeground(RDR_GOLD);
        star2.setBounds(1170, 50, 60, 60);
        panel.add(star2);
    }

    private void startGame() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your cowboy name!", 
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
        
        String[] columnNames = {"Username", "Skor", "Peluru Meleset", "Sisa Peluru"};
        Object[][] data;
        
        if (historyList.isEmpty()) {
            data = new Object[][] {
                {"---", "---", "---", "---"},
                {"---", "---", "---", "---"},
                {"---", "---", "---", "---"}
            };
        } else {
            int rows = Math.min(3, historyList.size());
            data = new Object[3][4];
            
            for (int i = 0; i < rows; i++) {
                model.HistoryModel h = historyList.get(i);
                data[i][0] = h.getUsername();
                data[i][1] = String.valueOf(h.getScore());
                data[i][2] = String.valueOf(h.getBulletsMissed());
                data[i][3] = String.valueOf(h.getAmmo());
            }
            
            for (int i = rows; i < 3; i++) {
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
        statsTable.setFont(new Font("Monospaced", Font.PLAIN, 16));
        statsTable.setRowHeight(50);
        statsTable.getTableHeader().setFont(new Font("Serif", Font.BOLD, 18));
        statsTable.getTableHeader().setBackground(new Color(139, 90, 43));
        statsTable.getTableHeader().setForeground(RDR_CREAM);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
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
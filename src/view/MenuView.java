package view;

import presenter.GamePresenter;
import model.PlayerStatsModel;
import model.HistoryModel;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * MenuView - Layar menu utama dengan tema Red Dead Redemption
 * Menampilkan leaderboard, input username, dan tombol aksi
 */
public class MenuView extends JFrame {

    // ==================== KOMPONEN ====================
    private final GamePresenter presenter;
    private BufferedImage backgroundImage;
    private BufferedImage logoImage;
    private JTextField usernameField;
    private JTable statsTable;
    
    // ==================== FONT ====================
    private Font rdrFont;
    private Font rdrFontLarge;
    
    // ==================== TEMA WARNA RDR2 ====================
    private static final Color RDR_RED = new Color(180, 30, 30);
    private static final Color RDR_BRIGHT_RED = new Color(220, 50, 50);
    private static final Color RDR_CREAM = new Color(240, 230, 210);
    private static final Color RDR_DARK = new Color(15, 10, 8);
    private static final Color RDR_BLACK = new Color(20, 15, 10);
    
    // ==================== UKURAN WINDOW ====================
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    /**
     * Constructor - Inisialisasi menu view
     */
    public MenuView(GamePresenter presenter) {
        this.presenter = presenter;
        
        setTitle("HIDE AND SEEK REDEMPTION");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        loadAssets();
        initComponents();
    }

    // ==================== LOADING ASSETS ====================

    /**
     * Load semua asset (font, background, logo)
     */
    private void loadAssets() {
        loadFonts();
        loadBackground();
    }

    /**
     * Load custom font Western/RDR style
     * Fallback ke Serif jika tidak ditemukan
     */
    private void loadFonts() {
        String[] fontPaths = {
            "assets/fonts/rdr.ttf",
            "assets/fonts/western.ttf",
            "assets/rdr.ttf"
        };

        Font baseFont = tryLoadFont(fontPaths);
        
        if (baseFont != null) {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(baseFont);
            rdrFont = baseFont.deriveFont(Font.PLAIN, 18f);
            rdrFontLarge = baseFont.deriveFont(Font.BOLD, 56f);
            System.out.println("✓ Custom font loaded");
        } else {
            // Fallback font
            rdrFont = new Font("Serif", Font.BOLD, 18);
            rdrFontLarge = new Font("Serif", Font.BOLD, 56);
            System.out.println("⚠ Using fallback font");
        }
    }

    /**
     * Coba load font dari berbagai path
     */
    private Font tryLoadFont(String[] paths) {
        for (String path : paths) {
            File fontFile = new File(path);
            if (fontFile.exists()) {
                try {
                    return Font.createFont(Font.TRUETYPE_FONT, fontFile);
                } catch (Exception e) {
                    System.err.println("Error loading font: " + path);
                }
            }
        }
        return null;
    }

    /**
     * Load background image
     * Buat default jika tidak ditemukan
     */
    private void loadBackground() {
        String[] possiblePaths = {
            "assets/sprites/menu_background.png",
            "assets/menu_background.png",
            "assets/sprites/background.png",
            "assets/background.png"
        };

        backgroundImage = tryLoadImage(possiblePaths);
        
        if (backgroundImage == null) {
            backgroundImage = createDefaultBackground();
            System.out.println("⚠ Using default background");
        } else {
            System.out.println("✓ Background loaded");
        }
    }


    /**
     * Coba load image dari berbagai path
     */
    private BufferedImage tryLoadImage(String[] paths) {
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    return ImageIO.read(file);
                } catch (Exception e) {
                    System.err.println("Error loading image: " + path);
                }
            }
        }
        return null;
    }

    /**
     * Buat background default dengan gradient merah-coklat
     */
    private BufferedImage createDefaultBackground() {
        BufferedImage img = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(60, 20, 15),
            0, WINDOW_HEIGHT, new Color(30, 10, 8)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        g.dispose();
        return img;
    }

    // ==================== UI INITIALIZATION ====================

    /**
     * Inisialisasi semua komponen UI
     */
    private void initComponents() {
        JPanel mainPanel = createMainPanel();
        
        // Logo atau Title
        addLogoOrTitle(mainPanel);
        
        // Tombol aksi
        addActionButtons(mainPanel);
        
        // Tabel leaderboard
        addStatsTable(mainPanel);
        
        // Input username
        addUsernamePanel(mainPanel);
        
        setContentPane(mainPanel);
    }

    /**
     * Buat main panel dengan background custom
     */
    private JPanel createMainPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                }
                
                // Vignette overlay (efek gelap di pinggir)
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
    }

    /**
     * Tambah logo atau title text ke panel
     */
    private void addLogoOrTitle(JPanel panel) {
        panel.setLayout(null);
        
        if (logoImage != null) {
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
            logoLabel.setBounds(120, 30, 400, 150);
            panel.add(logoLabel);
        } else {
            JLabel titleLabel = createTitleLabel();
            titleLabel.setBounds(120, 30, 400, 150);
            panel.add(titleLabel);
        }
    }

    /**
     * Buat label title dengan efek shadow
     */
    private JLabel createTitleLabel() {
        return new JLabel("HIDE AND SEEK", SwingConstants.LEFT) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String mainText = "HIDE AND SEEK";
                String subtitle = "REDEMPTION II";
                
                // Main title dengan shadow
                g2d.setFont(getFont());
                int x = 10;
                int y = 60;
                
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.drawString(mainText, x + 3, y + 3);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(mainText, x, y);
                
                // Subtitle merah
                g2d.setFont(getFont().deriveFont(Font.BOLD, 24f));
                g2d.setColor(RDR_BRIGHT_RED);
                g2d.drawString(subtitle, x, y + 35);
            }
        };
    }

    /**
     * Tambah tombol START dan QUIT
     */
    private void addActionButtons(JPanel panel) {
        // Tombol START
        JButton playButton = createStyledButton("START", RDR_BRIGHT_RED);
        playButton.setBounds(480, 120, 320, 80);
        playButton.addActionListener(e -> startGame());
        panel.add(playButton);

        // Tombol QUIT
        JButton quitButton = createStyledButton("QUIT", RDR_DARK);
        quitButton.setBounds(820, 120, 260, 80);
        quitButton.addActionListener(e -> System.exit(0));
        panel.add(quitButton);
    }

    /**
     * Buat tombol dengan style RDR
     */
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Warna berdasarkan state
                Color color = baseColor;
                if (getModel().isPressed()) {
                    color = baseColor.darker();
                } else if (getModel().isRollover()) {
                    color = baseColor.brighter();
                }

                // Background
                g2d.setColor(color);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Border hitam tebal
                g2d.setColor(RDR_BLACK);
                g2d.setStroke(new BasicStroke(5));
                g2d.drawRect(0, 0, getWidth(), getHeight());

                // Text dengan shadow
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.drawString(getText(), textX + 2, textY + 2);
                
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

    /**
     * Tambah tabel leaderboard
     */
    private void addStatsTable(JPanel panel) {
        JPanel tablePanel = createStatsTablePanel();
        tablePanel.setBounds(140, 230, 1000, 250);
        panel.add(tablePanel);
    }

    /**
     * Buat panel tabel dengan data dari database
     */
    private JPanel createStatsTablePanel() {
        JPanel containerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background gelap
                g2d.setColor(new Color(0, 0, 0, 220));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Border merah
                g2d.setColor(RDR_RED);
                g2d.setStroke(new BasicStroke(6));
                g2d.drawRect(0, 0, getWidth(), getHeight());
            }
        };
        
        containerPanel.setLayout(new BorderLayout(0, 0));
        containerPanel.setOpaque(false);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load dan setup tabel
        statsTable = createStyledTable();
        updateTableData();
        
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    /**
     * Buat tabel dengan styling RDR
     */
    private JTable createStyledTable() {
        String[] columnNames = {"USERNAME", "SCORE", "MISSED SHOT", "AMMO LEFT"};
        Object[][] emptyData = {
            {"---", "---", "---", "---"},
            {"---", "---", "---", "---"}
        };
        
        JTable table = new JTable(emptyData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Table styling
        table.setFont(new Font("SansSerif", Font.BOLD, 20));
        table.setRowHeight(70);
        table.setGridColor(RDR_BLACK);
        table.setShowGrid(true);
        table.setBackground(RDR_RED);
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(RDR_BRIGHT_RED);
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(2, 2));
        
        // Header styling
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 18));
        table.getTableHeader().setBackground(RDR_BLACK);
        table.getTableHeader().setForeground(RDR_CREAM);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, RDR_RED));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        
        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(RDR_RED);
        centerRenderer.setForeground(Color.WHITE);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        return table;
    }

    /**
     * Update data tabel dari database
     */
    private void updateTableData() {
    // 1. Ambil semua data dari presenter
    List<HistoryModel> historyList = presenter.loadHistory();
    
    String[] columnNames = {"USERNAME", "SCORE", "MISSED SHOT", "AMMO LEFT"};
    
    // 2. Buat ukuran array data dinamis sesuai jumlah historyList
    // Jika list kosong, kita beri 1 baris placeholder agar tabel tidak hilang total
    int rowCount = Math.max(1, historyList.size());
    Object[][] data = new Object[rowCount][4];
    
    if (historyList.isEmpty()) {
        // Data placeholder jika history kosong
        data[0] = new Object[]{"---", "---", "---", "---"};
    } else {
        // 3. Masukkan semua data tanpa batasan angka 2
        for (int i = 0; i < historyList.size(); i++) {
            HistoryModel h = historyList.get(i);
            data[i][0] = h.getUsername() != null ? h.getUsername().toUpperCase() : "UNKNOWN";
            data[i][1] = String.valueOf(h.getScore());
            data[i][2] = String.valueOf(h.getBulletsMissed());
            data[i][3] = String.valueOf(h.getAmmo());
        }
    }
    
    // 4. Update model tabel
    statsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Tabel tetap tidak bisa diedit manual
        }
    });
    
    // 5. Terapkan kembali gaya visual
    applyTableStyling();
    }

    /**
     * Apply styling ke tabel setelah update data
     */
    private void applyTableStyling() {
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

    /**
     * Tambah panel input username
     */
    private void addUsernamePanel(JPanel mainPanel) {
        JPanel usernamePanel = createUsernameInputPanel();
        usernamePanel.setBounds(300, 500, 680, 60);
        mainPanel.add(usernamePanel);
    }

    /**
     * Buat panel input username
     */
    private JPanel createUsernameInputPanel() {
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

    // ==================== ACTIONS ====================

    /**
     * Mulai game dengan validasi username
     */
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

    /**
     * Simpan statistik saat ini (legacy method)
     */
    private void saveCurrentStats() {
        for (int i = 0; i < statsTable.getRowCount(); i++) {
            String username = (String) statsTable.getValueAt(i, 0);
            if (username.equals("---")) continue;
            
            int score = parseIntSafe((String) statsTable.getValueAt(i, 1));
            int remaining = parseIntSafe((String) statsTable.getValueAt(i, 3));
            
            PlayerStatsModel stats = new PlayerStatsModel(username);
            stats.setScore(score);
            stats.setBulletsRemaining(remaining);
            
            presenter.addPlayerStats(stats);
        }
    }

    /**
     * Parse string ke int dengan safe handling
     */
    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Override setVisible - Refresh data saat menu ditampilkan
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateTableData();
        }
        super.setVisible(visible);
    }
}
package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.BanditModel;
import model.BulletModel;
import model.EnemyBulletModel;
import model.PlayerModel;
import model.RockModel;
import presenter.GamePresenter;

// ...existing code...
public class GamePanel extends JPanel {

    private List<BulletModel> bullets;
    private List<EnemyBulletModel> enemyBullets;
    private List<BanditModel> bandits;
    private BufferedImage rockSprite;
    private PlayerModel player;
    private Image background;
    private Image playerSprite;
    private Image banditSprite;

    private List<RockModel> rocks;
    private GamePresenter presenter;

    public GamePanel(PlayerModel player, List<BulletModel> bullets, List<RockModel> rocks, List<BanditModel> bandits, List<EnemyBulletModel> enemyBullets   ) {
        this.player = player;
        this.bullets = bullets;
        this.rocks = rocks;
        this.bandits = bandits;
        this.enemyBullets = enemyBullets;
        setBackground(Color.BLACK);

        try {
                background = ImageIO.read(new File("assets/sprites/background.png"));
                playerSprite = ImageIO.read(new File("assets/sprites/player.png"));
                rockSprite = ImageIO.read(new File("assets/sprites/rock.png"));
                banditSprite = ImageIO.read(new File("assets/sprites/bandit.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private void drawStatusBar(Graphics g) {
    int barWidth = 200;
    int barHeight = 16;

    // === HP BAR ===
    g.setColor(Color.WHITE);
    g.drawString("HP", 20, 25);

    g.setColor(Color.RED);
    int hpWidth = (int)((player.getHp() / (double)player.getMaxHp()) * barWidth);
    g.fillRect(20, 30, hpWidth, barHeight);

    g.setColor(Color.WHITE);
    g.drawRect(20, 30, barWidth, barHeight);

    // === AMMO BAR ===
    g.drawString("AMMO", 20, 65);

    g.setColor(Color.YELLOW);
    int ammoWidth = (int)((player.getAmmo() / (double)player.getMaxAmmo()) * barWidth);
    g.fillRect(20, 70, ammoWidth, barHeight);

    g.setColor(Color.WHITE);
    g.drawRect(20, 70, barWidth, barHeight);

    g.setColor(Color.WHITE);
    g.drawString("SCORE: " + player.getScore(), 20, 110);

    }



    // setter agar presenter bisa diberikan dari luar (mis. GamePresenter)
    public void setPresenter(GamePresenter presenter) {
        this.presenter = presenter;
        // pasang listener setelah presenter diset untuk menghindari NullPointer
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (GamePanel.this.presenter != null) {
                    GamePanel.this.presenter.shoot(e.getX(), e.getY());
                }
            }
        });
    }


    public void setRocks(List<RockModel> rocks) {
        this.rocks = rocks;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw background
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);

        // draw player
        g.drawImage(
            playerSprite,
            player.getX(),
            player.getY(),
            40,
            40,
            null
        );

        // draw bullets
        g.setColor(Color.YELLOW);
        if (bullets != null) {
            for (BulletModel b : bullets) {
                g.fillOval(b.getX(), b.getY(), b.getSize(), b.getSize());
            }
        }
        g.setColor(Color.RED);
        if (enemyBullets != null) {
            for (EnemyBulletModel eb : enemyBullets) {
                g.fillOval(eb.getX(), eb.getY(), eb.getSize(), eb.getSize());
            }
        }

        if (rocks != null) {
            for (RockModel rock : rocks) {
                g.drawImage(
                    rockSprite,
                    rock.getX(),
                    rock.getY(),
                    rock.getWidth(),
                    rock.getHeight(),
                    null
                );
            }
        }

        if (bandits != null) {
            for (BanditModel bandit : bandits) {
                g.drawImage(
                    banditSprite,
                    bandit.getX(),
                    bandit.getY(),
                    40,
                    40,
                    null
                );
            }
        }


        drawStatusBar(g);
    }

}
// ...existing code...
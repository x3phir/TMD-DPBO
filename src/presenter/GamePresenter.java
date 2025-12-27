package presenter;

import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.swing.Timer;
import model.HistoryModel;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import model.BanditModel;
import model.BulletModel;
import model.EnemyBulletModel;
import model.PlayerModel;
import model.RockModel;
import database.Database;
import util.GameThread;
import view.GamePanel;
import view.GameView;
import view.MenuView;

public class GamePresenter {
    private ArrayList<BanditModel> bandits = new ArrayList<>();
    private ArrayList<BulletModel> bullets = new ArrayList<>();
    private ArrayList<EnemyBulletModel> enemyBullets = new ArrayList<>();
    private ArrayList<RockModel> rocks = new ArrayList<>();
    private GameThread gameThread;
    private MenuView menuView;
    private GameView gameView;
    private PlayerModel player;

    public GamePresenter() {
        menuView = new MenuView(this);
        gameView = new GameView(this);
    }

    public void showMenu() {
        menuView.setVisible(true);
        gameView.setVisible(false);
    }

    public void startGame() {
        initRocks();
        bullets.add(new BulletModel(100, 100, 5, 0)); // contoh peluru awal
        System.out.println("Starting game with " + rocks.size() + " rocks." + " Bullets count: " + bullets.size());
        menuView.setVisible(false);

        player = new PlayerModel(380, 260, "player1");
        GamePanel panel = new GamePanel(player, bullets, rocks, bandits, enemyBullets);
        panel.setRocks(rocks); // pastikan batu dikirim ke panel
        panel.setPresenter(this); // set presenter ke panel
        gameView.setGamePanel(panel);
        gameView.setVisible(true);

        gameThread = new GameThread(this);
        gameThread.start();

        new Timer(3000, e -> spawnBandit()).start();

        new Timer(1500, e -> {
        for (BanditModel b : bandits) {
        banditShoot(b);
        }
        }).start();

   }

    public void movePlayer(int dx, int dy) {
        int nextX = player.getX() + dx;
        int nextY = player.getY() + dy;

        Rectangle nextBounds = new Rectangle(nextX, nextY, 40, 40);

        for (RockModel rock : rocks) {
            if (nextBounds.intersects(rock.getBounds())) {
                return; // batal gerak
            }
        }

        player.move(dx, dy);
        gameView.refresh();
    }

    public void gameOver() {
        if (gameThread != null) {
            gameThread.stopGame();
        }
        gameView.setVisible(false);
        menuView.setVisible(true);
    }

    private void saveHistory() {
    String sql = "INSERT INTO history(username, score, ammo) VALUES(?,?,?)";

    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, player.getUsername());
        ps.setInt(2, player.getScore());
        ps.setInt(3, player.getAmmo());

        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }


      public void updateGame() {
    for (BulletModel b : bullets) {
        b.update();

        if (b.getX() < 0 || b.getX() > 800 ||
            b.getY() < 0 || b.getY() > 600) {
            b.deactivate();
        }
    }

    for (BanditModel b : bandits) {
    b.moveToward(
        player.getX(),
        player.getY()
    );
    }

    bullets.removeIf(b -> !b.isActive());

    for (EnemyBulletModel eb : enemyBullets) {
    eb.update();

    // KENA PLAYER
        if (eb.getBounds().intersects(player.getBounds())) {
            player.takeDamage(10);
            eb.deactivate();
            continue;
        }

        // MISS (keluar layar)
        if (eb.getX() < 0 || eb.getX() > 800 ||
            eb.getY() < 0 || eb.getY() > 600) {

            player.addAmmo(1);
            eb.deactivate();
        }
    }

    enemyBullets.removeIf(b -> !b.isActive());

    for (BulletModel bullet : bullets) {
        for (BanditModel bandit : bandits) {
            if (!bandit.isAlive()) continue;

            if (bullet.getBounds().intersects(bandit.getBounds())) {
                bandit.kill();
                bullet.deactivate();
                player.addScore(100);
                break;
            }
        }
    }

    bandits.removeIf(b -> !b.isAlive());
    bullets.removeIf(b -> !b.isActive());

    // CHECK HP - jika <= 0 maka game over
    if (player.getHp() <= 0) {
        saveHistory();
        gameOver();
        return;
    }

    gameView.refresh();
    }


    private void initRocks() {
        rocks.clear();
        rocks.add(new RockModel(200, 150, 64, 64));
        rocks.add(new RockModel(400, 300, 64, 64));
        System.out.println("Jumlah batu: " + rocks.size());
    }

    private void spawnBandit() {
    int x = (int)(Math.random() * 700) + 50;
    int y = (int)(Math.random() * 100) + 50;

    bandits.add(new BanditModel(x, y));
    }


    public void shoot(int mouseX, int mouseY) {
    if (player.getAmmo() <= 0) return;

    System.out.println("Shooting towards: " + mouseX + ", " + mouseY);

    int px = player.getX() + 20;
    int py = player.getY() + 20;

    double dx = mouseX - px;
    double dy = mouseY - py;
    double length = Math.sqrt(dx*dx + dy*dy);

    double speed = 8;
    double vx = (dx / length) * speed;
    double vy = (dy / length) * speed;

    bullets.add(new BulletModel(px, py, vx, vy));
    player.useAmmo();
    if (gameView != null) gameView.refresh();
    }

    private void banditShoot(BanditModel bandit) {

    double bx = bandit.getX() + 20;
    double by = bandit.getY() + 20;

    double px = player.getX() + 20;
    double py = player.getY() + 20;

    double dx = px - bx;
    double dy = py - by;
    double dist = Math.sqrt(dx*dx + dy*dy);

    if (dist == 0) return;

    double speed = 4;
    double vx = (dx / dist) * speed;
    double vy = (dy / dist) * speed;

    enemyBullets.add(new EnemyBulletModel(bx, by, vx, vy));
   }

   public List<HistoryModel> loadHistory(){
    List<HistoryModel> list = new ArrayList<>();

    String sql = "SELECT username, score, ammo FROM history ORDER BY score DESC";

    try (Connection c = Database.getConnection();
         Statement s = c.createStatement();
         ResultSet rs = s.executeQuery(sql)) {

        while (rs.next()) {
            list.add(new HistoryModel(
                rs.getString("username"),
                rs.getInt("score"),
                rs.getInt("ammo")
            ));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
   }

}
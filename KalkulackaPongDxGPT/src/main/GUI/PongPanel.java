import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PongPanel extends JPanel {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 40;
    private static final int BALL_SIZE = 10;

    private int paddleY;
    private int ballX;
    private int ballY;
    private int ballSpeedX;
    private int ballSpeedY;
    private boolean gameRunning;
    private boolean playerWins;

    private int aiPaddleY;
    private boolean aiEnabled;

    public PongPanel() {
        paddleY = HEIGHT / 2;
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballSpeedX = 2;
        ballSpeedY = 2;
        gameRunning = false;
        playerWins = false;

        aiPaddleY = HEIGHT / 2;
        aiEnabled = true;

        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                    paddleY -= 10; // Pohyb pálky nahoru
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    paddleY += 10; // Pohyb pálky dolů
                }

                // Omezte pohyb pálky v rámci herní plochy
                if (paddleY < 0) {
                    paddleY = 0;
                } else if (paddleY > HEIGHT - PADDLE_HEIGHT) {
                    paddleY = HEIGHT - PADDLE_HEIGHT;
                }
            }
        });
    }

    public void startGame() {
        gameRunning = true;
        playerWins = false;

        Timer timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveBall();
                moveAIPaddle();
                checkCollisions();
                repaint();
            }
        });
        timer.start();
    }

    public void stopGame() {
        gameRunning = false;
    }

    public void restartGame() {
        stopGame();
        paddleY = HEIGHT / 2;
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballSpeedX = 3;
        ballSpeedY = 3;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Kreslení herní plochy
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Kreslení hráčovy pálky
        g.setColor(Color.WHITE);
        g.fillRect(10, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Kreslení AI pálky
        g.setColor(Color.WHITE);
        g.fillRect(WIDTH - PADDLE_WIDTH - 10, aiPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Kreslení míčku
        g.setColor(Color.RED);
        g.fillOval(ballX - BALL_SIZE / 2, ballY - BALL_SIZE / 2, BALL_SIZE, BALL_SIZE);

        // Kreslení výsledku
        if (!gameRunning) {
            g.setColor(Color.WHITE);
            Font font = new Font("Arial", Font.BOLD, 24);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            String resultText = playerWins ? "Vyhrál jsi!" : "Prohrál jsi!";
            int textWidth = fm.stringWidth(resultText);
            int x = (WIDTH - textWidth) / 2;
            int y = HEIGHT / 2 + fm.getAscent() / 2;
            g.drawString(resultText, x, y);
        }
    }

    private void moveBall() {
        if (!gameRunning) {
            return;
        }

        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // Kolize s horní nebo dolní hranou herní plochy
        if (ballY - BALL_SIZE / 2 <= 0 || ballY + BALL_SIZE / 2 >= HEIGHT) {
            ballSpeedY = -ballSpeedY;
        }

        // Kolize s hráčovou pálkou
        if (ballX - BALL_SIZE / 2 <= PADDLE_WIDTH + 10 && ballY >= paddleY && ballY <= paddleY + PADDLE_HEIGHT) {
            ballSpeedX = -ballSpeedX;
        }

        // Kolize s AI pálkou
        if (ballX + BALL_SIZE / 2 >= WIDTH - PADDLE_WIDTH - 10 && ballY >= aiPaddleY && ballY <= aiPaddleY + PADDLE_HEIGHT) {
            ballSpeedX = -ballSpeedX;
        }
    }

    private void moveAIPaddle() {
        if (!gameRunning || !aiEnabled) {
            return;
        }

        int aiPaddleCenter = aiPaddleY + PADDLE_HEIGHT / 2;
        if (aiPaddleCenter < ballY) {
            aiPaddleY += 2;
        } else {
            aiPaddleY -= 2;
        }

        if (aiPaddleY < 0) {
            aiPaddleY = 0;
        } else if (aiPaddleY > HEIGHT - PADDLE_HEIGHT) {
            aiPaddleY = HEIGHT - PADDLE_HEIGHT;
        }
    }

    private void checkCollisions() {
        if (!gameRunning) {
            return;
        }

        // Kolize s levou stranou herní plochy - prohra
        if (ballX - BALL_SIZE / 2 <= 0) {
            gameRunning = false;
            playerWins = false;
        }

        // Kolize s pravou stranou herní plochy - výhra
        if (ballX + BALL_SIZE / 2 >= WIDTH) {
            gameRunning = false;
            playerWins = true;
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }


    public boolean isGameRunning() {
        return gameRunning;
    }
}

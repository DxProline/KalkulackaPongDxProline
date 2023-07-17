import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CalculatorGUI extends JFrame {
    private JTextField inputField;
    private PongPanel pongPanel;
    private JButton startButton;
    private JButton restartButton;

    public CalculatorGUI() {
        // Nastavení základních vlastností okna
        setTitle("Kalkulačka");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Vytvoření panelu pro tlačítka
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 4));

        // Vytvoření tlačítek
        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "sqrt", "x^2", "x^n", "C",
                "Hra"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        // Vytvoření textového pole pro vstup
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 24));
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        // Vytvoření panelu pro Pong
        pongPanel = new PongPanel();
        pongPanel.setVisible(false);

        // Vytvoření tlačítka pro ovládání hry
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pongPanel.isGameRunning()) {
                    pongPanel.stopGame();
                    startButton.setText("Start");
                    restartButton.setVisible(true);
                } else {
                    pongPanel.startGame();
                    startButton.setText("Stop");
                    restartButton.setVisible(false);
                }
            }
        });

        // Vytvoření tlačítka pro restart hry
        restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pongPanel.restartGame();
                startButton.setText("Stop");
                restartButton.setVisible(false);
            }
        });
        restartButton.setVisible(false);

        // Přidání komponent do okna
        getContentPane().add(inputField, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.CENTER);
        getContentPane().add(pongPanel, BorderLayout.SOUTH);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(startButton);
        controlPanel.add(restartButton);
        getContentPane().add(controlPanel, BorderLayout.EAST);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JButton button = (JButton) event.getSource();
            String buttonText = button.getText();
            String currentText = inputField.getText();

            if (buttonText.equals("=")) {
                try {
                    // Výpočet vstupního výrazu
                    double result = evaluateExpression(currentText);
                    inputField.setText(Double.toString(result));
                } catch (IllegalArgumentException e) {
                    // Chybný výraz
                    inputField.setText("Chybný výraz");
                }
            } else if (buttonText.equals("C")) {
                // Vymazání vstupu
                inputField.setText("");
            } else if (buttonText.equals("sqrt")) {
                // Odstranění mezer a výpočet odmocniny
                String expression = currentText.replace(" ", "");
                double result = Math.sqrt(Double.parseDouble(expression));
                inputField.setText(Double.toString(result));
            } else if (buttonText.equals("x^2")) {
                // Odstranění mezer a výpočet druhé mocniny
                String expression = currentText.replace(" ", "");
                double result = Math.pow(Double.parseDouble(expression), 2);
                inputField.setText(Double.toString(result));
            } else if (buttonText.equals("x^n")) {
                // Přechod do módu výpočtu mocniny
                inputField.setText(currentText + " ^ ");
            } else if (buttonText.equals("Hra")) {
                // Zobrazení nebo skrytí hry Pong
                if (pongPanel.isVisible()) {
                    pongPanel.setVisible(false);
                    startButton.setEnabled(false);
                    restartButton.setVisible(false);
                } else {
                    pongPanel.setVisible(true);
                    startButton.setEnabled(true);
                }
            }
        }

        private double evaluateExpression(String expression) {
            try {
                return new ExpressionEvaluator().evaluate(expression);
            } catch (Exception e) {
                throw new IllegalArgumentException("Chybný výraz");
            }
        }
    }

    private class PongPanel extends JPanel {
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

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (!aiEnabled) {
                        paddleY = e.getY();
                        if (paddleY < 0) {
                            paddleY = 0;
                        } else if (paddleY > HEIGHT - PADDLE_HEIGHT) {
                            paddleY = HEIGHT - PADDLE_HEIGHT;
                        }
                    }
                }
            });
        }

        public void startGame() {
            gameRunning = true;
            playerWins = false;
            startButton.setText("Stop");
            restartButton.setVisible(false);

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
            startButton.setText("Start");
            restartButton.setVisible(true);
        }

        public void restartGame() {
            stopGame();
            paddleY = HEIGHT / 2;
            ballX = WIDTH / 2;
            ballY = HEIGHT / 2;
            ballSpeedX = 3;
            ballSpeedY = 3;
            restartButton.setVisible(false);
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
                String resultText = playerWins ? "Vyhrál jsi!" : "Prohrál jsi!";
                FontMetrics fm = g.getFontMetrics(font);
                int textWidth = fm.stringWidth(resultText);
                int x = (WIDTH - textWidth) / 2;
                int y = HEIGHT / 2;
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
                restartButton.setVisible(true);
            }

            // Kolize s pravou stranou herní plochy - výhra
            if (ballX + BALL_SIZE / 2 >= WIDTH) {
                gameRunning = false;
                playerWins = true;
                restartButton.setVisible(true);
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(WIDTH, HEIGHT);
        }

        public boolean isGameRunning() {
            return gameRunning;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorGUI calculator = new CalculatorGUI();
            calculator.setVisible(true);
        });
    }
}

class ExpressionEvaluator {
    public double evaluate(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length())
                    throw new IllegalArgumentException("Chybný výraz");
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new IllegalArgumentException("Chybný výraz");
                }

                return x;
            }
        }.parse();
    }
}

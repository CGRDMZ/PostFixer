package com.deuceng.MathGame;

import enigma.console.Console;
import enigma.core.Enigma;
import enigma.event.TextMouseListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class PostFixerGame {
    private static final int SCREEN_WIDTH = 80;
    private static final int SCREEN_HEIGHT = 15;

    private static final int BOARD_HEIGHT = 10;
    private static final int BOARD_WIDTH = 10;

    private String[][] gameScreen;
    private long timeLeft;

    private static Console cn;

    private int mode;
    // 0 -> Free Mode
    // 1 -> Take Mode
    // 2 -> Evaluation Mode

    private long start;
    private long timeElapsed;

    private Player player;
    private Stack evaluation;
    private Queue input;

    private String number;

    public TextMouseListener tmlis;
    public KeyListener klis;

    private boolean isStart;

    public int keypr;   // key pressed?
    public int rkey;    // key   (for press/release)

    public PostFixerGame() throws Exception {
        cn = Enigma.getConsole("#Post Fixer Game#", SCREEN_WIDTH, SCREEN_HEIGHT, 32);
        start = 0;
        number = "";
        isStart = true;
        this.player = new Player(0, 0, 0);
        this.evaluation = new Stack();
        this.input = new Queue(100000000);
        timeElapsed = 0;
        start = System.currentTimeMillis();
        this.run();
    }

    private void init() throws Exception {
        mode = 0;
        gameScreen = new String[10][10];
        for (int i = 0; i < gameScreen.length; i++) {
            for (int j = 0; j < gameScreen[i].length; j++) {
                gameScreen[i][j] = ".";
            }
        }

        timeElapsed = System.currentTimeMillis() - start;
    }

    private void update() throws Exception {
        eventHandler();
        fillInput();

        if (mode != 1)
            fillGameScreen();

        if (mode == 1)
            take();

//        if (mode == 0 && player.getBag().size() != 0) {
//            evaluate();
//        }

        player.move(BOARD_WIDTH, BOARD_HEIGHT);
        if (mode != 1)
            player.stop();

        if (mode == 1) {
            timeElapsed = (System.currentTimeMillis() - start);
        }
        timeLeft = (long) 60 - timeElapsed / 1000;
    }

    private void draw() {
        // displays the game board
        for (int i = 0; i < gameScreen.length; i++) {
            for (int j = 0; j < gameScreen[i].length; j++) {
                cn.getTextWindow().setCursorPosition(0 + j, i);
                cn.getWriter().print(gameScreen[i][j]);
            }

        }
        // displays the input queue
        for (int i = 0; i < input.size(); i++) {
            cn.getTextWindow().setCursorPosition(30 + i, 0);
            cn.getWriter().print(input.peek());
            input.enqueue(input.dequeue());
        }
        // displays the player's bag
        String s = "";

        cn.getTextWindow().setCursorPosition(30, 1);
        cn.getWriter().print("                                     ");
        for (int i = 0; i < player.getBag().size(); i++) {
            s += player.getBag().peek() + " ";
            player.getBag().enqueue(player.getBag().dequeue());
        }
        cn.getTextWindow().setCursorPosition(30, 1);
        cn.getWriter().print(s);

        // displays score
        cn.getTextWindow().setCursorPosition(30, 8);
        cn.getTextWindow().output("" + player.getScore());

        // displays the evaluation stack
        cn.getTextWindow().setCursorPosition(30, 7);
        cn.getWriter().print("                                    ");
        s = "";
        Stack temp = new Stack();

        for (int i = -evaluation.size(); i < 10; i++) {
            cn.getTextWindow().setCursorPosition(60, i);
            cn.getWriter().print("      ");
        }
        while (!evaluation.isEmpty()) {
            cn.getTextWindow().setCursorPosition(60, 10 - evaluation.size());
            cn.getWriter().print(evaluation.peek());
            temp.push(evaluation.pop());
        }

        while (!temp.isEmpty()) {
            evaluation.push(temp.pop());
        }

        // displays time
        cn.getTextWindow().setCursorPosition(30, 5);
        cn.getTextWindow().output(timeLeft + "         ");

        // displays the game mode
        switch (mode) {
            case 0:
                cn.getTextWindow().setCursorPosition(30, 6);
                cn.getTextWindow().output("Free Mode        ");
                break;
            case 1:
                cn.getTextWindow().setCursorPosition(30, 6);
                cn.getTextWindow().output("Take Mode        ");
                break;
            case 2:
                cn.getTextWindow().setCursorPosition(30, 6);
                cn.getTextWindow().output("Evaluation Mode  ");
        }

        cn.getTextWindow().setCursorPosition(player.getPosX(), player.getPosY());
        cn.getWriter().print('X');
    }

    private void take() {
        String symbol = gameScreen[player.getPosY()][player.getPosX()];

        if (!symbol.equals(".")) {
            if (symbol.equals("+") || symbol.equals("-") || symbol.equals("*") || symbol.equals("/")) {
                player.take(symbol);
                gameScreen[player.getPosY()][player.getPosX()] = ".";
                player.stop();
            } else {
                number += gameScreen[player.getPosY()][player.getPosX()];
                gameScreen[player.getPosY()][player.getPosX()] = ".";
                try {
                    String nextSymbol = gameScreen[player.getPosY() + player.getDirY()][player.getPosX() + player.getDirX()];
                    if (nextSymbol.equals(".") || nextSymbol.equals("+") || nextSymbol.equals("-") || nextSymbol.equals("*") || nextSymbol.equals("/")) {
                        player.stop();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        } else if (!number.equals("")) {
            player.take(number);
            number = "";
            player.stop();

        }

    }

    private boolean isOperator(String s) {
        return s.equals(".") || s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    private boolean isNumber(String s) {
        return !(s.equals(".") || s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/"));
    }

    public int calculateScore() {
        int score = 0;
        String previousSymbol = "";
        for (int i = 0; i < player.getBag().size(); i++) {
            String symbol = (String) player.getBag().peek();
            if (!player.getBag().peek().equals("+") && !player.getBag().peek().equals("-") && !player.getBag().peek().equals("*") && !player.getBag().peek().equals("/") && symbol.length() != 1) {
                score += symbol.length() * 2;
            } else if (!previousSymbol.equals("") && (isOperator(symbol) && isNumber(previousSymbol) || isOperator(previousSymbol) && isNumber(symbol))) {
                score += 2;
            } else {
                score += 1;
            }
            previousSymbol = symbol;
            player.getBag().enqueue(player.getBag().dequeue());
        }
        return score * score;
    }

    public void evaluate() {
        mode = 2;
        int size = player.getBag().size();

        String dequeue = (String) player.getBag().dequeue();
        try {
            if ("+".equals(dequeue)) {
                int number1 = Integer.parseInt((String) evaluation.pop());
                int number2 = Integer.parseInt((String) evaluation.pop());
                evaluation.push("" + (number1 + number2));
            } else if ("-".equals(dequeue)) {
                int number1 = Integer.parseInt((String) evaluation.pop());
                int number2 = Integer.parseInt((String) evaluation.pop());
                evaluation.push("" + (number1 - number2));
            } else if ("/".equals(dequeue)) {
                int number1 = Integer.parseInt((String) evaluation.pop());
                int number2 = Integer.parseInt((String) evaluation.pop());
                evaluation.push("" + (number1 / number2));
            } else if ("*".equals(dequeue)) {
                int number1 = Integer.parseInt((String) evaluation.pop());
                int number2 = Integer.parseInt((String) evaluation.pop());
                evaluation.push("" + (number1 * number2));
            } else {
                evaluation.push(dequeue);
            }
        } catch (EmptyStackException e) {
            // only the first time when the mode changes add player - 20
            player.addScore(-20);
        }
    }

    private int countSymbols() {
        int counter = 0;
        for (int i = 0; i < gameScreen.length; i++) {
            for (int j = 0; j < gameScreen[i].length; j++) {
                if (!gameScreen[i][j].equals(".")) counter++;
            }
        }
        return counter;
    }

    private void fillInput() {
        Random rand = new Random();
        while (input.size() < 8) {
            int number = rand.nextInt(13) + 1;
            if (number < 10) {
                input.enqueue(Integer.toString(number));
            } else if (number == 10) {
                input.enqueue("/");
            } else if (number == 11) {
                input.enqueue("*");
            } else if (number == 12) {
                input.enqueue("+");
            } else if (number == 13) {
                input.enqueue("-");
            }
        }
    }

    public void fillGameScreen() {
        while (countSymbols() < 40) {
            Random rand = new Random();
            fillInput();
            gameScreen[rand.nextInt(BOARD_HEIGHT)][rand.nextInt(BOARD_WIDTH)] = (String) input.dequeue();
        }
    }

    private void eventHandler() {
        if (keypr == 1) {
            long pauseTime = timeElapsed;
            if (!player.isMoving()) {
                if (rkey == KeyEvent.VK_RIGHT) player.goRight();
                if (rkey == KeyEvent.VK_LEFT) player.goLeft();
                if (rkey == KeyEvent.VK_UP) player.goUp();
                if (rkey == KeyEvent.VK_DOWN) player.goDown();
                if (rkey == KeyEvent.VK_SPACE) gameScreen[player.getPosY()][player.getPosX()] = ".";
                if (rkey == KeyEvent.VK_T) {
                    start = System.currentTimeMillis() - pauseTime;
                    mode = 1;


                    while (!evaluation.isEmpty()) {
                        evaluation.pop();
                    }
                }
                if (rkey == KeyEvent.VK_F) {
                    player.addScore(calculateScore());
                    mode = 0;
                    start += System.currentTimeMillis() - pauseTime;
                    isStart = false;

                }
                if (rkey == KeyEvent.VK_SPACE && mode == 0 && player.getBag().size() != 0) {
                    evaluate();
                }
            }

            keypr = 0;
        }
    }


    public void run() throws Exception {

        klis = new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                keypr = 1;
                rkey = e.getKeyCode();
            }

            public void keyReleased(KeyEvent e) {
            }
        };
        cn.getTextWindow().addKeyListener(klis);
//        cn.getTextWindow().setCursorType(1);
        init();
        while (true) {
            update();
            draw();
            if (mode == 2)
                mode = 0;
            Thread.sleep(200);


        }
    }
}

package com.deuceng.MathGame;

import enigma.console.Console;
import enigma.console.TextAttributes;
import enigma.core.Enigma;
import enigma.event.TextMouseListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class PostFixerGame {
    private static final int SCREEN_WIDTH = 70;
    private static final int SCREEN_HEIGHT = 15;

    private static final int BOARD_HEIGHT = 10;
    private static final int BOARD_WIDTH = 10;

    private static final int BOARD_X_OFFSET = 2;
    private static final int BOARD_Y_OFFSET = 2;

    private static final Color TURQUOISE = new Color(28, 174, 200);
    private static final Color WHITE = new Color(255, 255, 255);

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

    private boolean isPunishable;

    public int keypr;   // key pressed?
    public int rkey;    // key   (for press/release)

    public PostFixerGame() throws Exception {
        cn = Enigma.getConsole("#Post Fixer Game#", SCREEN_WIDTH, SCREEN_HEIGHT, 32);
        start = 0;
        number = "";
        this.player = new Player(0, 0, 0);
        this.evaluation = new Stack(100);
        this.input = new Queue(100000000);
        timeElapsed = 0;
        start = System.currentTimeMillis();
    }

    private void init() {
        mode = 0;
        gameScreen = new String[10][10];
        for (int i = 0; i < gameScreen.length; i++) {
            for (int j = 0; j < gameScreen[i].length; j++) {
                gameScreen[i][j] = ".";
            }
        }

        isPunishable = true;
        timeElapsed = 0;
        start = System.currentTimeMillis();
        timeElapsed = System.currentTimeMillis() - start;

        player.setScore(0);

        // reset player's bag, in case not all the evaluation done.
        while (!player.getBag().isEmpty()) {
            player.getBag().dequeue();
        }


        while (!evaluation.isEmpty()) {
            evaluation.pop();
        }
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

        if (timeLeft == 0) {
            init();
        }
    }

    private void draw() throws InterruptedException {

        //display the border
        for (int i = 0; i < gameScreen[0].length + 2; i++) {
            // # characters top and bottom
            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET - 1 + i, BOARD_Y_OFFSET - 1);
            cn.getWriter().print("#");

            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET - 1 + i, BOARD_Y_OFFSET + gameScreen.length);
            cn.getWriter().print("#");
        }

        for (int i = 0; i < gameScreen.length; i++) {
            // left and right borders
            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET - 1, BOARD_Y_OFFSET + i);
            cn.getWriter().print("#");

            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + gameScreen[0].length, BOARD_Y_OFFSET + i);
            cn.getWriter().print("#");
        }

        // drawing numbers around the board
        for (int i = 0; i < gameScreen[0].length; i++) {
            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + i, BOARD_Y_OFFSET - 2);
            cn.getWriter().print((i + 1) % 10);
        }

        for (int i = 0; i < gameScreen.length; i++) {
            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET - 2, BOARD_Y_OFFSET + i);
            cn.getWriter().print((i + 1) % 10);
        }

        // displays the game board
        for (int i = 0; i < gameScreen.length; i++) {
            for (int j = 0; j < gameScreen[i].length; j++) {
                cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + j, BOARD_Y_OFFSET + i);
                cn.getWriter().print(gameScreen[i][j]);
            }
        }

        // code for setting different colors
        cn.setTextAttributes(new TextAttributes(WHITE, TURQUOISE));
        for (int i = 0; i < 8; i++) {
            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + gameScreen[0].length + 14 + i, 9);
            cn.getWriter().print("<");

            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + gameScreen[0].length + 14 + i, 11);
            cn.getWriter().print("<");
        }
        // resetting to default
        cn.setTextAttributes(new TextAttributes(new Color(255, 255, 255)));

        // displays the input queue
        for (int i = 0; i < input.size(); i++) {
            cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + gameScreen[0].length + 14 + i, 10);
            cn.getWriter().print(input.peek());
            input.enqueue(input.dequeue());
        }


        // displays the player's bag
        String s = "";

        cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + gameScreen[0].length + 3, 2);
        cn.getWriter().print("Expression:");

        cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + gameScreen[0].length + 15, 2);
        cn.getWriter().print("                                     ");
        for (int i = 0; i < player.getBag().size(); i++) {
            s += player.getBag().peek() + " ";
            player.getBag().enqueue(player.getBag().dequeue());
        }
        cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + gameScreen[0].length + 15, 2);
        cn.getWriter().print(s);


        // displays score
        cn.getTextWindow().setCursorPosition(26, 5);
        cn.getTextWindow().output("Score:");

        cn.getTextWindow().setCursorPosition(32, 5);
        cn.getTextWindow().output("" + player.getScore());

        // displays the evaluation stack
        cn.getTextWindow().setCursorPosition(30, 7);
        cn.getWriter().print("                                    ");
        s = "";
        Stack temp = new Stack(100);

        for (int i = 0; i < 7; i++) {
            cn.getTextWindow().setCursorPosition(55, i + 3);
            cn.getWriter().print("|");
        }

        for (int i = 0; i < 7; i++) {
            cn.getTextWindow().setCursorPosition(66, i + 3);
            cn.getWriter().print("|");
        }

        for (int i = 0; i < 10; i++) {
            cn.getTextWindow().setCursorPosition(56 + i, 10);
            cn.getWriter().print("-");
        }

        cn.getTextWindow().setCursorPosition(55, 10);
        cn.getWriter().print("+");

        cn.getTextWindow().setCursorPosition(66, 10);
        cn.getWriter().print("+");

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
        cn.getTextWindow().setCursorPosition(26, 4);
        cn.getTextWindow().output("Time :");

        cn.getTextWindow().setCursorPosition(32, 4);
        cn.getTextWindow().output(timeLeft + "         ");

        // displays the game mode
        switch (mode) {
            case 0:
                cn.getTextWindow().setCursorPosition(26, 6);
                cn.getTextWindow().output("Mode :Free        ");
                break;
            case 1:
                cn.getTextWindow().setCursorPosition(26, 6);
                cn.getTextWindow().output("Mode :Take        ");
                break;
            case 2:
                cn.getTextWindow().setCursorPosition(26, 6);
                cn.getTextWindow().output("Mode :Evaluation  ");
        }

        cn.setTextAttributes(new TextAttributes(new Color(80, 243, 255)));
        cn.getTextWindow().setCursorPosition(BOARD_X_OFFSET + player.getPosX(), BOARD_Y_OFFSET + player.getPosY());
        cn.getWriter().print('X');
        cn.setTextAttributes(new TextAttributes(WHITE));


        if (mode == 2)
            mode = 0;
        Thread.sleep(200);
    }


    private void take() {
        String symbol = gameScreen[player.getPosY()][player.getPosX()];

        if (!symbol.equals(".")) {
            if (isOperator(symbol)) {
                player.take(symbol);
                gameScreen[player.getPosY()][player.getPosX()] = ".";
                player.stop();
            } else {
                number += gameScreen[player.getPosY()][player.getPosX()];
                gameScreen[player.getPosY()][player.getPosX()] = ".";
                try {
                    String nextSymbol = gameScreen[player.getPosY() + player.getDirY()][player.getPosX() + player.getDirX()];
                    if (nextSymbol.equals(".") || isOperator(nextSymbol)) {
                        player.stop();
                    }
                }catch (ArrayIndexOutOfBoundsException ignored){

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

    private int calculateScore() {
        int score = 0;
        String previousSymbol = "";
        for (int i = 0; i < player.getBag().size(); i++) {
            String symbol = (String) player.getBag().peek();

            if (!isOperator((String)player.getBag().peek()) && symbol.length() != 1) {
                score += symbol.length() * 2;
            } else if (!previousSymbol.equals("") && (isOperator(symbol) && isNumber(previousSymbol)
                    || (isOperator(previousSymbol) && isNumber(symbol)))) {
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
//        int size = player.getBag().size();

        String symbol = (String) player.getBag().dequeue();
        try {
            if ("+".equals(symbol)) {
                int number1 = Integer.parseInt((String) evaluation.pop());
                int number2 = Integer.parseInt((String) evaluation.pop());
                evaluation.push("" + (number2 + number1));
            } else if ("-".equals(symbol)) {
                int number1 = Integer.parseInt((String) evaluation.pop());
                int number2 = Integer.parseInt((String) evaluation.pop());
                evaluation.push("" + (number2 - number1));
            } else if ("/".equals(symbol)) {
                int number1 = Integer.parseInt((String) evaluation.pop());
                int number2 = Integer.parseInt((String) evaluation.pop());
                evaluation.push("" + (number2 / number1));
            } else if ("*".equals(symbol)) {
                int number1 = Integer.parseInt((String) evaluation.pop());
                int number2 = Integer.parseInt((String) evaluation.pop());
                evaluation.push("" + (number2 * number1));
            } else {
                evaluation.push(symbol);
            }
        } catch (EmptyStackException e) {
            // only the first time when the mode changes add player - 20
            if (isPunishable) {
                player.addScore(-20);
                isPunishable = !isPunishable;
            }
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
                if (rkey == KeyEvent.VK_RIGHT || rkey == KeyEvent.VK_D) player.goRight();
                if (rkey == KeyEvent.VK_LEFT || rkey == KeyEvent.VK_A) player.goLeft();
                if (rkey == KeyEvent.VK_UP || rkey == KeyEvent.VK_W) player.goUp();
                if (rkey == KeyEvent.VK_DOWN || rkey == KeyEvent.VK_S) player.goDown();
                if (rkey == KeyEvent.VK_SPACE) gameScreen[player.getPosY()][player.getPosX()] = ".";
                if (rkey == KeyEvent.VK_T) {
                    start = System.currentTimeMillis() - pauseTime;
                    mode = 1;
                    isPunishable = true;

                    // reset player's bag, in case not all the evaluation done.
                    while (!player.getBag().isEmpty()) {
                        player.getBag().dequeue();
                    }


                    while (!evaluation.isEmpty()) {
                        evaluation.pop();
                    }
                }
                if (rkey == KeyEvent.VK_F) {
                    player.addScore(calculateScore());
                    mode = 0;
                    start += System.currentTimeMillis() - pauseTime;



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
//        cn.getTextWindow().setCursorType(0);


        init();
        while (true) {
            update();
            draw();
        }
    }
}

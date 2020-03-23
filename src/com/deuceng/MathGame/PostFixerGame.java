package com.deuceng.MathGame;

import enigma.console.Console;
import enigma.core.Enigma;
import enigma.event.TextMouseListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class PostFixerGame {
    private static final int SCREEN_WIDTH = 45;
    private static final int SCREEN_HEIGHT = 15;
    private static Console cn;
    private long start;
    private long timeElapsed;
    private Player player;
    private Stack evaluation;
    private Queue input;


    private char[][] gameScreen;

    public TextMouseListener tmlis;
    public KeyListener klis;

    public int keypr;   // key pressed?
    public int rkey;    // key   (for press/release)

    public PostFixerGame() throws Exception {
        cn = Enigma.getConsole("#Post Fixer Game#", SCREEN_WIDTH, SCREEN_HEIGHT, 32);
        start = System.currentTimeMillis();
        this.player = new Player(0, 0, 0);
        this.evaluation = new Stack();
        this.input = new Queue(10000000);
        this.run();

    }

    private void init() throws Exception {
        gameScreen = new char[10][10];
        for (int i = 0; i < gameScreen.length; i++) {
            for (int j = 0; j < gameScreen[i].length; j++) {
                gameScreen[i][j] = '.';
            }
        }

        timeElapsed = System.currentTimeMillis() - start;
    }

    private void take() {
        try {
            if (Integer.parseInt(Character.toString(gameScreen[player.getPosY()][player.getPosX()])) < 10) {
                player.take(gameScreen[player.getPosY()][player.getPosX()]);
                gameScreen[player.getPosY()][player.getPosX()] = '.';
            }
        } catch (NumberFormatException e) {
        }
        try {
        if (gameScreen[player.getPosY()][player.getPosX()] != '.') {
            player.take(gameScreen[player.getPosY()][player.getPosX()]);
            gameScreen[player.getPosY()][player.getPosX()] = '.';
            player.stop();
        }
        }catch (NumberFormatException e) {
        }
    }

    private int countSymbols() {
        int counter = 0;
        for (int i = 0; i < gameScreen.length; i++) {
            for (int j = 0; j < gameScreen[i].length; j++) {
                if (gameScreen[i][j] != '.') counter++;
            }
        }
        return counter;
    }

    private void fillInput() {
        Random rand = new Random();
        while (input.size() < 8) {
            int number = rand.nextInt(13) + 1;
            if (number < 10) {
                input.enqueue(Integer.toString(number).charAt(0));
            } else if (number == 10) {
                input.enqueue('/');
            } else if (number == 11) {
                input.enqueue('*');
            } else if (number == 12) {
                input.enqueue('+');
            } else if (number == 13) {
                input.enqueue('-');
            }
        }
    }

    public void fillGameScreen() {
        while (countSymbols() < 40) {
            Random rand = new Random();
            fillInput();
            gameScreen[rand.nextInt(10)][rand.nextInt(10)] = (char) input.dequeue();
        }
    }

    private void eventHandler() {
        if (keypr == 1) {
            if (!player.isMoving()) {
            if (rkey == KeyEvent.VK_RIGHT) player.goRight();
            if (rkey == KeyEvent.VK_LEFT) player.goLeft();
            if (rkey == KeyEvent.VK_UP) player.goUp();
            if (rkey == KeyEvent.VK_DOWN) player.goDown();
            if (rkey == KeyEvent.VK_SPACE) gameScreen[player.getPosY()][player.getPosX()] = '.';
            }
            keypr = 0;
        }
    }

    private void update() {
        eventHandler();
        fillInput();
        fillGameScreen();
        take();
        player.move();
    }

    private void draw() {
        for (int i = 0; i < gameScreen.length; i++) {
            cn.getTextWindow().setCursorPosition(0, i);
            cn.getWriter().print(gameScreen[i]);
        }

        for (int i = 0; i < input.size(); i++) {
            cn.getTextWindow().setCursorPosition(30 + i, 0);
            cn.getWriter().print(input.peek());
            input.enqueue(input.dequeue());
        }

        for (int i = 0; i < player.getBag().size(); i++) {
            cn.getTextWindow().setCursorPosition(30 + i * 2, 1);
            cn.getWriter().print(player.getBag().peek());
            player.getBag().enqueue(player.getBag().dequeue());
        }
        cn.getTextWindow().setCursorPosition(player.getPosX(), player.getPosY());
        cn.getWriter().print('X');
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
            Thread.sleep(40);
            cn.getTextWindow().setCursorPosition(30, 5);
            timeElapsed = (System.currentTimeMillis() - start);
            cn.getTextWindow().output(String.valueOf(timeElapsed/ 1000));
        }
    }
}

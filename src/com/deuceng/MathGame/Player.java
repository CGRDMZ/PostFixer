package com.deuceng.MathGame;

public class Player {
    private int posX;
    private int posY;
    private int dirX;
    private int dirY;
    private int score;
    private CircularQueue bag;

    public Player(int posX, int posY, int score) {
        this.posX = posX;
        this.posY = posY;
        this.dirX = 0;
        this.dirY = 0;
        this.score = score;
        this.bag = new CircularQueue(1000);
    }

    public boolean isMoving() {
        return (dirX != 0 || dirY != 0);
    }

    public void take(String sym) {
        this.bag.enqueue(sym);
    }

    public void goRight() {
        this.dirX = 1;
        this.dirY = 0;
    }

    public void goLeft() {
        this.dirX = -1;
        this.dirY = 0;
    }

    public void goUp() {
        this.dirX = 0;
        this.dirY = -1;
    }

    public void goDown() {
        this.dirX = 0;
        this.dirY = 1;
    }

//    public void goBack() {
//        this.dirX *= -1;
//        this.dirY *= -1;
//    }

    public void move(int screenWidth, int screenHeight) {
        // border collision
        if ((this.dirX == 1 && this.posX + 1 >= screenWidth) || (this.dirX == -1 && this.posX <= 0) ||
                (this.dirY == 1 && this.posY + 1 >= screenHeight) || (this.dirY == -1 && this.posY <= 0)) this.stop();

        // movement
        this.posX += this.dirX;
        this.posY += this.dirY;
    }

    public void stop() {
        this.dirX = 0;
        this.dirY = 0;
    }

    public void addScore(int score) {
        this.score += score;
    }


    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }


    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDirX() {
        return dirX;
    }

    public void setDirX(int dirX) {
        this.dirX = dirX;
    }

    public int getDirY() {
        return dirY;
    }

    public void setDirY(int dirY) {
        this.dirY = dirY;
    }

    public CircularQueue getBag() {
        return bag;
    }

    public void setBag(CircularQueue bag) {
        this.bag = bag;
    }
}

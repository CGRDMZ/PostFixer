package com.deuceng.MathGame;

public class Queue {
    private Object[] elements;

    private int rear;
    private int front;

    public Queue(int capacity) {
        elements = new Object[capacity];
        rear = -1;
        front = 0;
    }

    public void enqueue(Object data) {
        if (isFull()) {
            System.out.println("queue is full!");
        }else {
            rear++;
            elements[rear] = data;
        }
    }

    public Object dequeue() {
        if (isEmpty()) {
            System.out.println("queue is empty.");
            return null;
        }else {
            Object data = elements[front];
            elements[front] = null;
            front++;
            return data;
        }
    }

    public Object peek() {
        if (isEmpty()) {
            System.out.println("queue is empty.");
            return null;
        }else {
            return elements[front];
        }
    }

    public boolean isFull() {
        return (rear + 1 == elements.length);
    }

    public boolean isEmpty() {
        return rear < front;
    }

    public int size() {
        return rear - front + 1;
    }
}

package com.deuceng.MathGame;

public class CircularQueue {
    private Object[] elements;

    private int rear;
    private int front;

    public CircularQueue(int capacity) {
        elements = new Object[capacity];
        rear = -1;
        front = 0;
    }

    public void enqueue(Object data) {
        if (isFull()) {
            System.out.println("queue overflow");
        } else {
            rear = (rear + 1) % elements.length;
            elements[rear] = data;
        }
    }

    public Object dequeue() {
        if (isEmpty()) {
            System.out.println("queue is empty");
            return null;
        } else {
            Object data = elements[front];
            elements[front] = null;
            front = (front + 1) % elements.length;
            return data;
        }
    }

    public Object peek() {
        if (isEmpty()) {
            System.out.println("queue is empty");
            return null;
        } else
            return elements[front];
    }

    public boolean isFull() {
        return front == (rear + 1) % elements.length && elements[front] != null
                && elements[rear] != null;
    }

    public boolean isEmpty() {
        return elements[front] == null;
    }

    public int size() {
        if (rear >= front) {
            return rear - front + 1;
        } else if (elements[front] != null) {
            return elements.length - (front - rear) + 1;
        } else
            return 0;
    }
}

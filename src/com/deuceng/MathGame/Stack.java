package com.deuceng.MathGame;

import java.util.EmptyStackException;

public class Stack {
	private int top;
	private Object[] elements;
	
	public Stack(int capacity){
		elements = new Object [capacity];
		top = -1;
	}
	
	boolean isFull() {
		return (top + 1 == elements.length);
	}
	
	boolean isEmpty() {
		return ( top == -1);
	}
	
	int size() {
		return top + 1;
	}
	
	void push (Object data) {
		if (isFull()) {
		}
		else {
			top++;
			elements[top] = data;						
		}		
	}
	
	Object pop() {
		if (isEmpty()) {
			throw new EmptyStackException();
		}
		else {
			Object retData = elements[top];
			top--;
			return retData;
		}
	}
	
	Object peek() {
		if (isEmpty()) {
			System.out.println("stack is overflow");
			return null;
		}
		else {
			return elements[top];
		}
	}
}

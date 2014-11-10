package com.example.magegame;

abstract public class Sprite
{
	protected int width;
	protected int height;
	protected int currentFrame = 0;
	protected double x;
	protected double y;
	protected double rotation;
	protected Controller mainController;
	protected boolean deleted = false;
	public Sprite()
	{}
	abstract public void frameCall();
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getRotation() {
		return rotation;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public boolean isDeleted() {
		return deleted;
	}
	
}
package com.example.magegame;

abstract public class Sprite
{
	protected double width;
	protected double height;
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
	public double getWidth() {
		return width;
	}
	public double getHeight() {
		return height;
	}
	public boolean isDeleted() {
		return deleted;
	}
	
}
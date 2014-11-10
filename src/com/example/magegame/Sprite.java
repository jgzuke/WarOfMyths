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
	abstract protected void frameCall();
	protected double getX() {
		return x;
	}
	protected double getY() {
		return y;
	}
	protected void setX(double x) {
		this.x = x;
	}
	protected void setY(double y) {
		this.y = y;
	}
	protected double getRotation() {
		return rotation;
	}
	protected double getWidth() {
		return width;
	}
	protected double getHeight() {
		return height;
	}
	protected boolean isDeleted() {
		return deleted;
	}
	
}
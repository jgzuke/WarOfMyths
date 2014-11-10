package com.example.magegame;
abstract public class Sprite
{
	public int width, height, currentFrame = 0;
	public double x, y, rotation;
	public Controller mainController;
	public boolean deleted = false;
	public Sprite()
	{}
	abstract public void frameCall();
}
package com.example.magegame;
abstract public class Wall
{
	protected Controller mainController;
	protected boolean changing;
	protected int playerRollWidth = 5;
	public Wall()
	{
	}
	abstract public void frameCall();
}
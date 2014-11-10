package com.example.magegame;
abstract public class Wall
{
	protected Controller mainController;
	protected int playerRollWidth = 5;
	protected int humanWidth = 10;
	protected boolean tall;
	abstract protected void frameCall();
}
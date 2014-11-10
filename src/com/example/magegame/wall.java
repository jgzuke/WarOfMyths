package com.example.magegame;
abstract public class Wall
{
	protected Controller mainController;
	protected int playerRollWidth = 5;
	abstract protected void frameCall();
}
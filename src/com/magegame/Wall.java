package com.magegame;
abstract public class Wall
{
	protected Controller control;
	protected int playerRollWidth = 5;
	protected int humanWidth = 10;
	protected boolean tall;
	abstract protected void frameCall();
}
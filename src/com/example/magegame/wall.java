package com.example.magegame;
abstract public class wall
{
	public Controller mainController;
	public boolean changing;
	public wall()
	{
	}
	abstract public void frameCall();
}
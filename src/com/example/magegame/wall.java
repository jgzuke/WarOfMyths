package com.example.magegame;
abstract public class Wall
{
	protected Controller mainController;
	protected boolean changing;
	public Wall()
	{
	}
	abstract public void frameCall();
}
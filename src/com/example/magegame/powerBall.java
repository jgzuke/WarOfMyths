package com.example.magegame;
abstract public class PowerBall extends DrawnSprite
{
	protected boolean hitBack;
	protected double xForward;
	protected double yForward;
	protected int power;
	protected double xDif = 0;
	protected double yDif = 0;
	protected double realX;
	protected double realY;@
	Override
	protected void frameCall()
	{
		realX += xForward;
		realY += yForward;
		x = (int) realX;
		y = (int) realY;
		power--;
		if(power < 5)
		{
			deleted = true;
		}
	}
}
package com.example.magegame;
abstract public class powerBall extends drawnSprite
{
	public boolean hitBack;
	public double XForward;
	public double YForward;
	public int power;
	public double xDif = 0;
	public double yDif = 0;
	public double realX;
	public double realY;@
	Override
	public void frameCall()
	{
		realX += XForward;
		realY += YForward;
		x = (int) realX;
		y = (int) realY;
		power--;
		if(power < 5)
		{
			deleted = true;
		}
	}
	public void checkHitBack(double X, double Y)
	{
		hitBack = false;
		if(X < 97.5 || X > 382.5 || Y < 17.5 || Y > 302.5)
		{
			hitBack = true;
		}
		if(hitBack == false)
		{
			for(int i = 0; i < mainController.obstaclesRectanglesX1.length; i++)
			{
				if(hitBack == false)
				{
					if(X > mainController.obstaclesRectanglesX1[i] && X < mainController.obstaclesRectanglesX2[i])
					{
						if(Y > mainController.obstaclesRectanglesY1[i] && Y < mainController.obstaclesRectanglesY2[i])
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(hitBack == false)
		{
			for(int i = 0; i < mainController.obstaclesCirclesX.length; i++)
			{
				if(hitBack == false)
				{
					if(Math.pow(X - mainController.obstaclesCirclesX[i], 2) + Math.pow(Y - mainController.obstaclesCirclesY[i], 2) < Math.pow(mainController.obstaclesCirclesRadius[i], 2))
					{
						hitBack = true;
					}
				}
			}
		}
	}
}
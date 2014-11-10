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
	public void frameCall()
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
	public void checkHitBack(double X, double Y)
	{
		hitBack = false;
		if(X < 97.5 || X > 382.5 || Y < 17.5 || Y > 302.5)
		{
			hitBack = true;
		}
		if(hitBack == false)
		{
			for(int i = 0; i < mainController.getCurrentRectangle(); i++)
			{
				if(hitBack == false)
				{
					if(X > mainController.getObstaclesRectanglesX1(i) && X < mainController.getObstaclesRectanglesX2(i))
					{
						if(Y > mainController.getObstaclesRectanglesY1(i) && Y < mainController.getObstaclesRectanglesY2(i))
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(hitBack == false)
		{
			for(int i = 0; i < mainController.getCurrentCircle(); i++)
			{
				if(hitBack == false)
				{
					if(Math.pow(X - mainController.getObstaclesCirclesX(i), 2) + Math.pow(Y - mainController.getObstaclesCirclesY(i), 2) < Math.pow(mainController.getObstaclesCirclesRadius(i), 2))
					{
						hitBack = true;
					}
				}
			}
		}
	}
}
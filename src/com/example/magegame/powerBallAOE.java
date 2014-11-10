package com.example.magegame;
abstract public class PowerBallAOE extends DrawnSprite
{
	protected boolean normal;
	protected double widthDone = 0;
	protected double xDif = 0;
	protected double yDif = 0;
	protected byte alpha;
	protected int alphaDown;
	protected int timeToDeath;
	protected boolean damaging = true;
	public PowerBallAOE(Controller creator, int X, int Y, double Power, boolean Shrinking)
	{
		normal = Shrinking;
		mainController = creator;
		x = X;
		y = Y;
		width = 2;
		height = 2;
		alpha = (byte) 254;
		if(!normal)
		{
			timeToDeath = 8;
			width = 20+(timeToDeath*10);
			height = 20+(timeToDeath*10);
			alpha = (byte)(6);
		} else
		{
			timeToDeath = (int) (Math.pow(Power, 0.65) / 3.5)+5;
		}
		alphaDown = (int)(254/timeToDeath);
	}
	@ Override
	protected void frameCall()
	{
		timeToDeath--;
		if(timeToDeath == 5)
		{
			damaging = true;
			if(!normal)
			{
				explode();
				deleted = true;
			}
		}
		if(normal)
		{
			width += 10;
			height += 10;
			alpha -= alphaDown;
		} else
		{
			width -= 10;
			height -= 10;
			alpha += alphaDown;
		}
		if(timeToDeath == 0)
		{
			deleted = true;
		}
		widthDone = 7.5 + (width / 2);
	}
	protected byte getAlpha() {
		return alpha;
	}
	abstract protected void explode();
	@ Override
	protected double getWidth() {
		return width*1.5;
	}
	@ Override
	protected double getHeight() {
		return height*1.5;
	}
}
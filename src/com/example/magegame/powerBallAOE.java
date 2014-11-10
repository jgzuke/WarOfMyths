package com.example.magegame;
abstract public class PowerBallAOE extends DrawnSprite
{
	protected double widthDone = 0;
	protected double xDif = 0;
	protected double yDif = 0;
	protected byte alpha;
	protected int timeToDeath;@
	Override
	protected void frameCall()
	{
		timeToDeath--;
		if(timeToDeath == 0)
		{
			deleted = true;
		}
		width += 10;
		height += 10;
		alpha -= 10;
		widthDone = 7.5 + (width / 2);
	}
	protected byte getAlpha() {
		return alpha;
	}
}
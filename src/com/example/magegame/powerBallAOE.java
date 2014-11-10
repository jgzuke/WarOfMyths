package com.example.magegame;
abstract public class powerBallAOE extends drawnSprite
{
	public double widthDone = 0;
	public double xDif = 0;
	public double yDif = 0;
	public byte alpha;
	public int timeToDeath;@
	Override
	public void frameCall()
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
}
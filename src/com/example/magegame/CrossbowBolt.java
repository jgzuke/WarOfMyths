/*
 *
 */
package com.example.magegame;
public final class CrossbowBolt extends PowerBall
{
	public CrossbowBolt(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		mainController = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		xForward = Xforward;
		yForward = Yforward;
		visualImage = mainController.imageLibrary.arrow;
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		if(mainController.checkHitBack(x, y) && !deleted)
		{
			x -= xForward;
			y -= yForward;
			deleted = true;
			mainController.activity.playEffect("arrowhit");
		}
		if(mainController.player.getRollTimer() < 1 && !deleted)
		{
			xDif = x - mainController.player.x;
			yDif = y - mainController.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				mainController.player.getHit(power*7);
				deleted = true;
				mainController.activity.playEffect("arrowhit");
				if(mainController.getRandomDouble()*(0.5+mainController.getDifficultyLevelMultiplier()) > 1.7)
				{
					mainController.player.rads = Math.atan2(yForward, xForward);
					mainController.player.stun();
				}
			}
		}
	}
}
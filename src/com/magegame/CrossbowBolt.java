/*
 *
 */
package com.magegame;
public final class CrossbowBolt extends PowerBall
{
	public CrossbowBolt(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		control = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		xForward = Xforward;
		yForward = Yforward;
		visualImage = control.imageLibrary.arrow;
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		if(control.checkHitBack(x, y) && !deleted)
		{
			x -= xForward;
			y -= yForward;
			deleted = true;
			control.activity.playEffect("arrowhit");
		}
		if(control.player.currentFrame < 22 && !deleted)
		{
			xDif = x - control.player.x;
			yDif = y - control.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				control.player.getHit(power*7);
				deleted = true;
				control.activity.playEffect("arrowhit");
				if(control.getRandomDouble()*(0.5+control.getDifficultyLevelMultiplier()) > 1.7)
				{
					control.player.rads = Math.atan2(yForward, xForward);
					control.player.stun();
				}
			}
		}
	}
}
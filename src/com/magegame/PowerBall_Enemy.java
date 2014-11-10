/*
 *
 */
package com.magegame;
public final class PowerBall_Enemy extends PowerBall
{
	public PowerBall_Enemy(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		control = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		xForward = Xforward;
		yForward = Yforward;
		visualImage = control.imageLibrary.powerBall_ImageEnemy[control.getRandomInt(5)];
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
			control.createPowerBallEnemyAOE(x, y, 10, true);
			deleted = true;
			control.activity.playEffect("electric");
		}
		if(control.player.currentFrame < 22 && !deleted)
		{
			xDif = x - control.player.x;
			yDif = y - control.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				control.player.getHit(power);
				control.createPowerBallEnemyAOE(x, y, power, true);
				deleted = true;
				control.activity.playEffect("electric");
				if(control.getRandomDouble()*(0.5+control.getDifficultyLevelMultiplier()) > 1.2)
				{
					control.player.rads = Math.atan2(yForward, xForward);
					control.player.stun();
				}
			}
		}
	}
}
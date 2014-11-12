/**
 * behavior for enemy power ball, much like crossbowbolt
 */
package com.magegame;
public final class PowerBall_Enemy extends PowerBall
{
	/**
	 * Sets position, speed, power, and direction of travel
	 * @param creator control object
	 * @param X starting x position
	 * @param Y starting y position
	 * @param Power power bolt was fired with
	 * @param Xforward bolts x velocity
	 * @param Yforward bolts y velocity
	 * @param Rotation bolts direction of travel
	 */
	public PowerBall_Enemy(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		control = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		xForward = Xforward;
		yForward = Yforward;
		visualImage = control.imageLibrary.shotEnemy;
		setImageDimensions();
		power = Power;
		rotation = Rotation;
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
	/**
	 * Checks whether projectile hits obstacles or player
	 */
	@ Override
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
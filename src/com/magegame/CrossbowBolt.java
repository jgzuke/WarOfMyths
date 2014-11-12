/**
 * Defines behavior for enemy crossbowmans projectiles
 */
package com.magegame;
public final class CrossbowBolt extends PowerBall
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
	public CrossbowBolt(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
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
		if(control.player.currentFrame < 22 && !deleted) // currentframe under 22 because if player rolls it doesnt hit
		{
			xDif = x - control.player.x;
			yDif = y - control.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100) // if player within 10 pixels
			{
				control.player.getHit(power*4);
				deleted = true;
				control.activity.playEffect("arrowhit");
				if(control.getRandomDouble()*(0.5+control.getDifficultyLevelMultiplier()) > 1.7) // chance of stunning player
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
			deleted = true;
			control.activity.playEffect("arrowhit");
		}
		if(control.checkHitBack(x-(xForward/2), y-(yForward/2)) && !deleted)
		{
			deleted = true;
			control.activity.playEffect("arrowhit");
		}
		if(control.player.currentFrame < 22 && !deleted) // currentframe under 22 because if player rolls it doesnt hit
		{
			xDif = x - control.player.x;
			yDif = y - control.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100) // if player within 10 pixels
			{
				control.player.getHit(power*4);
				deleted = true;
				control.activity.playEffect("arrowhit");
				if(control.getRandomDouble()*(0.5+control.getDifficultyLevelMultiplier()) > 1.7) // chance of stunning player
				{
					control.player.rads = Math.atan2(yForward, xForward);
					control.player.stun();
				}
			}
		}
	}
}
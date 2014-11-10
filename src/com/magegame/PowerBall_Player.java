/**
 * behavior for player power ball
 */
package com.magegame;
public final class PowerBall_Player extends PowerBall
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
	protected PowerBall_Player(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		control = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		xForward = Xforward;
		yForward = Yforward;
		visualImage = control.imageLibrary.powerBall_ImagePlayer[control.getRandomInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}
	/**
	 * checks whether power ball hits any enemies
	 */
	@ Override
	protected void frameCall()
	{
		super.frameCall();
		if(control.checkHitBack(x, y) || control.checkHitBack(x-(xForward/2), y-(yForward/2)) && !deleted)
		{
			explodeBack();
			control.activity.playPlayerEffect();
		}
		if(control.enemyInView(x, y))
		{
			for(int i = 0; i < control.enemies.length; i++)
			{
				if(control.enemies[i] != null && !deleted && control.enemies[i].getRollTimer() < 1)
				{
					control.enemies[i].setLevels(control.enemies[i].getLevelCurrentPosition(), x, y, xForward, yForward);
					control.enemies[i].incrementLevelCurrentPosition();
					xDif = x - control.enemies[i].x;
					yDif = y - control.enemies[i].y;
					if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 600)
					{
						control.activity.playPlayerEffect();
						power*=Math.pow((double)control.activity.wAres/10, 0.5);
						if(control.player.humanType == 0)
						{
							power *= Math.pow(control.player.spMod, 0.7);
						}
						control.enemies[i].getHit((int)power);
						explode();
					}
				}
			}
		}
	}
	/**
	 * explodes power ball when it hits back
	 */
	public void explodeBack()
	{
		x -= xForward;
		y -= yForward;
		control.createPowerBallPlayerAOE(x, y, 10);
		deleted = true;
	}
	/**
	 * explodes power ball when it hits enemy
	 */
	public void explode()
	{
		control.createPowerBallPlayerAOE(x, y, power);
		deleted = true;
	}
}
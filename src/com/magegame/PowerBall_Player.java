/**
 * behavior for player power ball
 */
package com.magegame;

import android.util.Log;

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
	private DrawnSprite target;
	private double r2d = 180/Math.PI;
	private double speed;
	private int rotChange;
	protected PowerBall_Player(Controller creator, int X, int Y, int Power, double Speed, double Rotation)
	{
		control = creator;
		speed = Speed;
		xForward = Math.cos(Rotation/r2d) * Speed;
		yForward = Math.sin(Rotation/r2d) * Speed;
		x = X;
		y = Y;
		if(control.checkHitBack(x, y))
		{
			explodeBack();
			control.activity.playPlayerEffect();
		}
		x +=(xForward*2);
		y +=(yForward*2);
		if(control.checkHitBack(x, y) && !deleted)
		{
			explodeBack();
			control.activity.playPlayerEffect();
		}
		realX = x;
		realY = y;
		visualImage = control.imageLibrary.powerBall_ImagePlayer[control.getRandomInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
		rotChange = 6+(control.activity.bTracking*1);
		while(rotation<0)
		{
			rotation+=360;
		}
	}
	/**
	 * checks whether power ball hits any enemies
	 */
	@ Override
	protected void frameCall()
	{
		super.frameCall();
		if(control.checkHitBack(x+(xForward*5), y+(yForward*5)))
		{
			int tempRotation = (int)Math.round(rotation/90)*90;
			if(Math.abs(tempRotation-rotation)<20)
			{
				rotation = tempRotation;
				xForward = Math.cos(rotation/r2d) * speed;
				yForward = Math.sin(rotation/r2d) * speed;
			}
		}
		if(control.checkHitBack(x, y) || control.checkHitBack(x-(xForward/2), y-(yForward/2)) && !deleted)
		{
			explodeBack();
			control.activity.playPlayerEffect();
		}
		if(target != null)
		{
			xDif = x - target.x;
			yDif = y - target.y;
			double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
			if(distance > 40000)
			{
				target = null;
			} else
			{
				double newRotation = Math.atan2(yDif, xDif) * r2d;
				newRotation -= 180;
				double fix = compareRot(newRotation/r2d);
				if(fix!=400)
				{
					if(fix>rotChange/2)
					{
						rotation += rotChange;
					} else if(fix<-rotChange/2)
					{
						rotation -= rotChange;
					} else
					{
						rotation += fix;
					}
					xForward = Math.cos(rotation/r2d) * speed;
					yForward = Math.sin(rotation/r2d) * speed;
				} else
				{
					target = null;
				}
			}
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
					double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
					if(distance < 600)
					{
						control.activity.playPlayerEffect();
						power*=Math.pow((double)control.activity.wAres/10, 0.5);
						if(control.player.humanType == 0)
						{
							power *= Math.pow(control.player.spMod, 0.7);
						}
						control.enemies[i].getHit((int)power);
						explode();
					} else if(distance < 14000)
					{
						if(target == null)
						{
							if(!control.checkObstructionsPointAll((float)x, (float)y, (float)control.enemies[i].x, (float)control.enemies[i].y))
							{
								target = control.enemies[i];
							}
						} else if(control.enemies[i]!=target)
						{
							if(Math.abs(compareRot(Math.atan2(yDif, xDif)))<Math.abs(compareRot(Math.atan2(y - target.y, x - target.x))))
							{
								if(!control.checkObstructionsPointAll((float)x, (float)y, (float)control.enemies[i].x, (float)control.enemies[i].y))
								{
									target = control.enemies[i];
								}
							}
						}
					}
				}
			}
			for(int i = 0; i < control.structures.length; i++)
			{
				if(control.structures[i] != null && !deleted)
				{
					xDif = x - control.structures[i].x;
					yDif = y - control.structures[i].y;
					double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
					if(distance < 600)
					{
						control.activity.playPlayerEffect();
						power*=Math.pow((double)control.activity.wAres/10, 0.5);
						if(control.player.humanType == 0)
						{
							power *= Math.pow(control.player.spMod, 0.7);
						}
						control.structures[i].getHit((int)power);
						explode();
					} else if(distance < 14000)
					{
						if(target == null)
						{
							if(!control.checkObstructionsPointAll((float)x, (float)y, (float)control.structures[i].x, (float)control.structures[i].y))
							{
								target = control.structures[i];
							}
						} else if(control.structures[i]!=target)
						{
							if(Math.abs(compareRot(Math.atan2(yDif, xDif)))<Math.abs(compareRot(Math.atan2(y - target.y, x - target.x))))
							{
								if(!control.checkObstructionsPointAll((float)x, (float)y, (float)control.structures[i].x, (float)control.structures[i].y))
								{
									target = control.structures[i];
								}
							}
						}
					}
				}
			}
		}
	}
	public double compareRot(double newRotation)
	{
		newRotation*=r2d;
		double fix = 400;
		while(newRotation<0)
		{
			newRotation+=360;
		}
		while(rotation<0)
		{
			rotation+=360;
		}
		if(newRotation>290 && rotation<70) newRotation-=360;
		if(rotation>290 && newRotation<70) rotation-=360;
		if(Math.abs(newRotation-rotation) < 45)
		{
			fix = newRotation-rotation;
		}
		return fix;
	}
	/**
	 * explodes power ball when it hits back
	 */
	public void explodeBack()
	{
		x -= xForward;
		y -= yForward;
		control.createPowerBallPlayerAOE(x, y, 40, false);
		deleted = true;
	}
	/**
	 * explodes power ball when it hits enemy
	 */
	public void explode()
	{
		control.createPowerBallPlayerAOE(x, y, power, true);
		deleted = true;
	}
}
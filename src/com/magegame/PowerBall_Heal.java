/**
 * behavior for player power ball
 */
package com.magegame;

import android.util.Log;

public final class PowerBall_Heal extends PowerBall
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
	private Enemy target;
	private double r2d = 180/Math.PI;
	private double speed;
	private int rotChange;
	protected PowerBall_Heal(Controller creator, int X, int Y, int Power, double Speed, double Rotation)
	{
		control = creator;
		speed = Speed;
		xForward = Math.cos(Rotation/r2d) * Speed;
		yForward = Math.sin(Rotation/r2d) * Speed;
		x = X;
		y = Y;
		if(control.checkHitBack(x, y))
		{
			fizzle();
		}
		x +=(xForward*2);
		y +=(yForward*2);
		if(control.checkHitBack(x, y) && !deleted)
		{
			fizzle();
		}
		realX = x;
		realY = y;
		visualImage = control.imageLibrary.shotPlayer;
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
		if(control.checkHitBack(x, y) || control.checkHitBack(x-(xForward/2), y-(yForward/2)) && !deleted)
		{
			fizzle();
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
						control.enemies[i].getHit(-250*control.getDifficultyLevelMultiplier());
						fizzle();
					} else if(distance < 14000)
					{
						if(target == null)
						{
							target = control.enemies[i];
						} else if(control.enemies[i]!=target)
						{
							if(Math.abs(compareRot(Math.atan2(yDif, xDif)))<Math.abs(compareRot(Math.atan2(y - target.y, x - target.x))))
							{
								target = control.enemies[i];
							}
						}
					}
				}
			}
		}
	}
	public void fizzle()
	{
		//control.createPowerBallPlayerAOE(x, y, 40, false);
		deleted = true;
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
}
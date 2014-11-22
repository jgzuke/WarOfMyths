/**
 * behavior for player power ball
 */
package com.magegame;

import android.util.Log;

public final class Proj_Tracker_Player extends Proj_Tracker
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
	protected Proj_Tracker_Player(Controller creator, int X, int Y, int Power, double Speed, double Rotation)
	{
		control = creator;
		speed = Speed;
		xForward = Math.cos(Rotation/r2d) * Speed;
		yForward = Math.sin(Rotation/r2d) * Speed;
		x = X;
		y = Y;
		if(control.checkHitBack(x, y, false))
		{
			explodeBack();
		}
		x +=(xForward);
		y +=(yForward);
		if(control.checkHitBack(x, y, false) && !deleted)
		{
			explodeBack();
		}
		realX = x;
		realY = y;
		visualImage = control.imageLibrary.shotPlayer;
		setImageDimensions();
		power = Power;
		rotation = Rotation;
		rotChange = 6+(control.activity.buyExtraTracking*1);
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
		if(target != null)
		{
			xDif = x - target.x;
			yDif = y - target.y;
			double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
			if(target.deleted) target = null;
			double newRotation = Math.atan2(yDif, xDif) * r2d;
			newRotation -= 180;
			double fix = compareRot(newRotation/r2d);
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
		}
		if(control.enemyInView(x, y))
		{
			for(int i = 0; i < control.enemies.size(); i++)
			{
				if(control.enemies.get(i) != null && !deleted && control.enemies.get(i).action.equals("Nothing"))
				{
					control.enemies.get(i).setLevels(control.enemies.get(i).levelCurrentPosition, x, y, xForward, yForward);
					control.enemies.get(i).levelCurrentPosition++;
				}
			}
		}
	}
	public double compareRot(double newRotation)
	{
		newRotation*=r2d;
		double fix = 400;
		while(newRotation<0) newRotation+=360;
		while(rotation<0) rotation+=360;
		if(newRotation>290 && rotation<70) newRotation-=360;
		if(rotation>290 && newRotation<70) rotation-=360;
		fix = newRotation-rotation;
		return fix;
	}
	@ Override
	/**
	 * explodes power ball when it hits back
	 */
	public void explodeBack()
	{
		control.createProj_TrackerPlayerAOE((int) realX, (int) realY, 30, false);
		deleted = true;
	}
	@ Override
	/**
	 * explodes power ball when it hits enemy
	 */
	public void explode()
	{
		control.createProj_TrackerPlayerAOE((int) realX, (int) realY, power/2, true);
		deleted = true;
	}
	@Override
	protected void hitTarget(int x, int y)
	{
		for(int i = 0; i < control.enemies.size(); i++)
		{
			if(control.enemies.get(i) != null && !deleted && !control.enemies.get(i).action.equals("Roll"))
			{
				xDif = x - control.enemies.get(i).x;
				yDif = y - control.enemies.get(i).y;
				double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
				if(distance < 600)
				{
					power*=Math.pow((double)control.activity.buyUpgradeAttack/10, 0.5);
					control.enemies.get(i).getHit((int)power);
					explode();
				}
			}
		}
		for(int i = 0; i < control.structures.size(); i++)
		{
			if(control.structures.get(i) != null && !deleted)
			{
				xDif = x - control.structures.get(i).x;
				yDif = y - control.structures.get(i).y;
				double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
				if(distance < 600)
				{
					power*=Math.pow((double)control.activity.buyUpgradeAttack/10, 0.5);
					control.structures.get(i).getHit((int)power);
					explode();
				}
			}
		}
	}
	@Override
	protected void hitBack(int x, int y)
	{
		if(control.checkHitBack(x, y, false) && !deleted)
		{
			explodeBack();
		}
	}
}
/**
 * Handles behavior for powerups
 */
package com.magegame;
public final class PowerUp extends DrawnSprite
{
	protected int ID;
	/**
	 * sets image, dimensions, and position
	 * @param creator control object
	 * @param X starting x value
	 * @param Y starting y value
	 * @param Type type of powerup
	 */
	public PowerUp(Controller creator, double X, double Y, int Type)
	{
		width = 30;
		height = 30;
		control = creator;
		x = X;
		y = Y;
		if(Type == 0)
		{
			ID = control.getRandomInt(6)+1;
		} else
		{
			ID=Type;
		}
		visualImage = control.imageLibrary.powerUps[ID-1];
	}
	/**
	 * checks whether player is close enough to pick up
	 */
	@ Override
	protected void frameCall()
	{
		double xDif = x - control.player.x;
		double yDif = y - control.player.y;
		double dist = Math.pow(xDif, 2) + Math.pow(yDif, 2);
		if(dist < 625)
		{			
			control.player.getPowerUp(ID);
			deleted = true;
		}		
		if(dist < 30000)
		{
			double rads = Math.atan2(yDif, xDif);
			x -= Math.cos(rads)*4000/dist;
			y -= Math.sin(rads)*4000/dist;
		}
	}
}
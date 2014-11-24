/**
 * Handles behavior for powerups
 */
package com.magegame;

import android.util.Log;

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
		ID=Type;
		if(ID == 0)
		{
			ID = control.getRandomInt(6)+1;
		}
		visualImage = control.imageLibrary.powerUps[ID-1];
	}
	/**
	 * checks whether player is close enough to pick up
	 */
	@ Override
	protected void frameCall()
	{
		if(Math.abs(x - control.player.x)<20&&Math.abs(y - control.player.y)<20)
		{			
			control.player.getPowerUp(ID);
			deleted = true;
		}
	}
}
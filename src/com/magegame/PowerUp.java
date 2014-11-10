/*
 * Handles visuals for player or enemy teleportation
 */
package com.magegame;
public final class PowerUp extends DrawnSprite
{
	private int ID;
	private int startInt = 1;
	public PowerUp(Controller creator, double X, double Y, int Type)
	{
		width = 30;
		height = 30;
		control = creator;
		x = X;
		y = Y;
		int playerType = control.playerType;
		if(Type == 0)
		{
			ID = control.getRandomInt(6)+1;
			while((ID==3&&playerType==1)||(ID==4&&playerType==3)||(ID==5&&playerType==2)||(ID==6&&playerType==0))
			{
				ID = control.getRandomInt(6)+1;
			}
		} else
		{
			ID=Type;
		}
		visualImage = control.imageLibrary.powerUps[ID-1];
	}@
	Override
	protected void frameCall()
	{
		double xDif = x - control.player.x;
		double yDif = y - control.player.y;
		if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 625)
		{			
			control.player.getPowerUp(ID);
			deleted = true;
		}
	}
}
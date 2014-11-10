/*
 * Handles visuals for player or enemy teleportation
 */
package com.example.magegame;
public final class PowerUp extends DrawnSprite
{
	private int ID;
	private int startInt = 1;
	public PowerUp(Controller creator, double X, double Y, int Type)
	{
		width = 30;
		height = 30;
		mainController = creator;
		x = X;
		y = Y;
		int playerType = mainController.playerType;
		if(Type == 0)
		{
			ID = mainController.getRandomInt(6)+1;
			while((ID==3&&playerType==1)||(ID==4&&playerType==3)||(ID==5&&playerType==2)||(ID==6&&playerType==0))
			{
				ID = mainController.getRandomInt(6)+1;
			}
		} else
		{
			ID=Type;
		}
		visualImage = mainController.imageLibrary.powerUps[ID-1];
	}@
	Override
	protected void frameCall()
	{
		double xDif = x - mainController.player.x;
		double yDif = y - mainController.player.y;
		if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 625)
		{			
			mainController.player.getPowerUp(ID);
			deleted = true;
		}
	}
}
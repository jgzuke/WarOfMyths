/*
 * Handles visuals for player or enemy teleportation
 */
package com.example.magegame;
public final class PowerUp extends DrawnSprite
{
	private int powerID;
	private int startInt = 1;
	private int timeToDeath = 200;
	public PowerUp(Controller creator, double X, double Y)
	{
		width = 30;
		height = 30;
		mainController = creator;
		x = X;
		y = Y;
		powerID = mainController.getRandomInt(6)+1;
		visualImage = mainController.imageLibrary.powerUps[powerID-1];
	}@
	Override
	protected void frameCall()
	{
		timeToDeath--;
		if(timeToDeath == 0)
		{
			deleted = true;
		}
		double xDif = x - mainController.player.x;
		double yDif = y - mainController.player.y;
		if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 625)
		{			
			mainController.player.getPowerUp(powerID);
			deleted = true;
		}
	}
}
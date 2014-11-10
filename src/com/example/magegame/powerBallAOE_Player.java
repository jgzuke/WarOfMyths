package com.example.magegame;
public final class PowerBallAOE_Player extends PowerBallAOE
{
	public PowerBallAOE_Player(Controller creator, int X, int Y, double Power)
	{
		mainController = creator;
		x = X;
		y = Y;
		visualImage = mainController.imageLibrary.powerBallAOE_Image[mainController.getPlayerType()];
		width = 10;
		height = 10;
		timeToDeath = (int) Power / 15;
		alpha = (byte) 200;
		if(mainController.player.getSp() > 90)
		{
			mainController.player.lowerSp(90);
		}
	}@
	Override
	public void frameCall()
	{
		super.frameCall();
		xDif = x - mainController.enemy.x;
		yDif = y - mainController.enemy.y;
		if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone)
		{
			mainController.enemy.getHit(60);
		}
                for(int i = 0; i < mainController.enemies.length; i++)
                {
                    if(mainController.enemies[i] != null)
			{
                            xDif = x - mainController.enemies[i].x;
		yDif = y - mainController.enemies[i].y;
		if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone)
		{
			mainController.enemies[i].getHit(60);
		}
                        }
                }
	}
}
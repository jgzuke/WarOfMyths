package com.example.magegame;
public final class PowerBallAOE_Enemy extends PowerBallAOE
{
	public PowerBallAOE_Enemy(Controller creator, int X, int Y, double Power)
	{
		mainController = creator;
		x = X;
		y = Y;
		visualImage = mainController.imageLibrary.powerBallAOE_Image[mainController.getEnemyType()];
		width = 10;
		height = 10;
		timeToDeath = (int) Power / 15;
		alpha = (byte) 200;
	}@
	Override
	public void frameCall()
	{
		super.frameCall();
		xDif = x - mainController.player.x;
		yDif = y - mainController.player.y;
		if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone)
		{
			if(mainController.enemy.humanType == 0)
			{
				mainController.player.getHit((int)(60*(1+mainController.enemy.getSp())));
			} else
			{
				mainController.player.getHit(60);
			}
		}
	}
}
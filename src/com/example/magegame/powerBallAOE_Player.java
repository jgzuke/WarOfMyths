package com.example.magegame;
public final class powerBallAOE_Player extends powerBallAOE
{
	public powerBallAOE_Player(Controller creator, int X, int Y, double Power)
	{
		mainController = creator;
		x = X;
		y = Y;
		visualImage = mainController.game.imageLibrary.powerBallAOE_Image[mainController.PlayerType];
		width = 10;
		height = 10;
		timeToDeath = (int) Power / 15;
		alpha = (byte) 200;
		if(mainController.player.Sp > 90)
		{
			mainController.enemy.HpMax -= 50;
			mainController.player.Sp -= 90;
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
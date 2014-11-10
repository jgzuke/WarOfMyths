package com.example.magegame;
public final class powerBallAOE_Enemy extends powerBallAOE
{
	public powerBallAOE_Enemy(Controller creator, int X, int Y, double Power)
	{
		mainController = creator;
		x = X;
		y = Y;
		visualImage = mainController.game.imageLibrary.powerBallAOE_Image[mainController.EnemyType];
		width = 10;
		height = 10;
		timeToDeath = (int) Power / 15;
		alpha = (byte) 200;
		if(mainController.enemy.Sp > 90)
		{
			mainController.player.HpMax -= 50;
			mainController.enemy.Sp -= 90;
		}
	}@
	Override
	public void frameCall()
	{
		super.frameCall();
		xDif = x - mainController.player.x;
		yDif = y - mainController.player.y;
		if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone)
		{
			mainController.player.getHit(60);
		}
	}
}